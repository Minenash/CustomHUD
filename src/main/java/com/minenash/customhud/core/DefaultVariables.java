package com.minenash.customhud.core;

import com.minenash.customhud.core.elements.FunctionalElement;
import com.minenash.customhud.core.elements.RealTimeElement;
import com.minenash.customhud.core.errors.ErrorType;
import com.minenash.customhud.core.errors.Errors;
import com.minenash.customhud.core.registry.VariableRegistry;
import com.minenash.customhud.mc1_20.elements.icon.SpaceElement;

import java.text.SimpleDateFormat;

public class DefaultVariables {

    public static void register() {

        VariableRegistry.register("customhud:space", context -> {
            if (!context.base().startsWith("space:")) return null;

            String widthStr = context.base().substring(6);
            try {
                return new SpaceElement( Integer.parseInt(widthStr) );
            }
            catch (NumberFormatException e) {
                Errors.addError(context, ErrorType.NOT_A_WHOLE_NUMBER, "\"" + widthStr + "\"");
                return new FunctionalElement.Error();
            }

        });

        VariableRegistry.register("customhud:real_time", context -> {
            if (!context.base().startsWith("real_time:")) return null;

            try {
                return new RealTimeElement(new SimpleDateFormat(context.base().substring(10)));
            }
            catch (IllegalArgumentException e) {
                Errors.addError(context, ErrorType.INVALID_TIME_FORMAT, e.getMessage());
                return new FunctionalElement.Error();
            }

        });
    }

}
