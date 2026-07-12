package net.ironedge.libraryofiron.render.umar.state;

@FunctionalInterface
public interface UMaterialCondition {
    boolean test(UMaterialStateContext context);
}