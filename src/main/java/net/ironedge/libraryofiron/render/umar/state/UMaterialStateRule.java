package net.ironedge.libraryofiron.render.umar.state;

import net.ironedge.libraryofiron.render.umar.material.UMaterialParamSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class UMaterialStateRule {

    private final UMaterialCondition condition;
    private final List<UMaterialDriver> drivers;

    private UMaterialStateRule(Builder builder) {
        this.condition = Objects.requireNonNull(builder.condition, "condition");
        this.drivers = List.copyOf(builder.drivers);
    }

    public boolean test(UMaterialStateContext context) {
        return condition.test(context);
    }

    public void apply(UMaterialStateContext context, UMaterialParamSet params) {
        for (UMaterialDriver driver : drivers) {
            driver.apply(context, params);
        }
    }

    public static Builder builder(UMaterialCondition condition) {
        return new Builder(condition);
    }

    public static final class Builder {
        private final UMaterialCondition condition;
        private final List<UMaterialDriver> drivers = new ArrayList<>();

        public Builder(UMaterialCondition condition) {
            this.condition = Objects.requireNonNull(condition, "condition");
        }

        public Builder driver(UMaterialDriver driver) {
            this.drivers.add(Objects.requireNonNull(driver, "driver"));
            return this;
        }

        public UMaterialStateRule build() {
            return new UMaterialStateRule(this);
        }
    }
}