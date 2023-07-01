package com.minenash.customhud.core;

import com.minenash.customhud.core.elements.FunctionalElement;
import com.minenash.customhud.core.elements.RealTimeElement;
import com.minenash.customhud.core.elements.ToggleElement;
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

        VariableRegistry.register("customhud:toggle", context -> {
            if (!context.startsWith("toggle:")) return null;

            String code = context.base().substring(7);
            try {
                int scancode = Integer.parseInt(code);
                System.out.println("WATCH: " + scancode);
                context.profile().toggles.put(scancode, false);
                return new ToggleElement(scancode);
            }
            catch (NumberFormatException e) {
                code = code.toUpperCase();
                if (code.length() == 1) {
                    context.profile().toggles.put((int) code.charAt(0), false);
                    return new ToggleElement(code.charAt(0));
                }
                if (code.length() == 2 && code.charAt(0) == 'N' && Character.isDefined( code.charAt(1) )) {
                    context.profile().toggles.put((int) code.charAt(1), false);
                    return new ToggleElement(code.charAt(1));
                }
                Errors.addError(context, ErrorType.INVALID_KEY, "\"" + code + "\"");
                return new FunctionalElement.Error();
            }
        });
    }

}
