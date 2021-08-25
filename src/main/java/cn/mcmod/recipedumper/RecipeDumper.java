package cn.mcmod.recipedumper;

import cn.mcmod.recipedumper.impl.DumpRecipeCommand;
import cn.mcmod.recipedumper.api.IRecipeDumper;
import cn.mcmod.recipedumper.impl.ModArgumentType;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

import java.util.Set;

@Mod("recipedumper")
public class RecipeDumper {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public RecipeDumper() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onSetup);
        MinecraftForge.EVENT_BUS.register(this);
        registerAllDumpers();
    }

    public void onSetup(FMLCommonSetupEvent event) {
        ArgumentTypes.register("mod", ModArgumentType.class, new ArgumentSerializer<>(() -> ModArgumentType.INSTANCE));
    }

    @SubscribeEvent
    public void onRegisterCommand(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("dumprecipe")
                .then(Commands.argument("mod", ModArgumentType.INSTANCE)
                        .executes(DumpRecipeCommand::executeCommand)
                )
        );
    }

    private void registerAllDumpers() {
        Type dumperForType = Type.getType(IRecipeDumper.For.class);
        for (ModFileScanData scanData : ModList.get().getAllScanData()) {
            Set<ModFileScanData.AnnotationData> annotations = scanData.getAnnotations();
            annotations.stream()
                    .filter(annotationData -> annotationData.getAnnotationType().equals(dumperForType))
                    .filter(annotationData -> {
                        Object modDeps = annotationData.getAnnotationData().get("modDeps");
                        return modDeps == null || ModList.get().isLoaded(((String) modDeps));
                    })
                    .map(ModFileScanData.AnnotationData::getClassType)
                    .distinct()
                    .forEach(this::registerThisDumper);
        }
    }

    private void registerThisDumper(Type dumperType) {
        String className = dumperType.getClassName();
        try {
            Class<?> clazz = Class.forName(className);
            if (IRecipeDumper.class.isAssignableFrom(clazz)) {
                IRecipeDumper<?> recipeDumper = (IRecipeDumper<?>) clazz.newInstance();
                IRecipeDumper.For annotation = clazz.getAnnotation(IRecipeDumper.For.class);
                if (annotation != null) {
                    DumpRecipeCommand.addRecipeDumper(annotation.value(), recipeDumper);
                    return;
                }
                for (IRecipeDumper.For aFor : clazz.getAnnotation(IRecipeDumper.Container.class).value()) {
                    DumpRecipeCommand.addRecipeDumper(aFor.value(), recipeDumper);
                }
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

}
