#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME};

#end
import org.spongepowered.asm.mixin.Mixin;

#parse("File Header.java")
@Mixin(value = ${NAME}.class, remap = false)
public abstract class ${NAME}Mixin {
}