package com.minenash.customhud.core.registry;

public record MetaData(
        String name,
        String desc,
        String cat,
        String[] vars,
        String flags,
        String versionAdded,
        boolean deprecated) {

    public record MetaDataBuilder(String[] vars, String flags) {

        public void meta(String cat, String name)               { meta(cat, name, "", "Unknown", false);   }
        public void metaD(String cat, String name)              { meta(cat, name, "", "Unknown", true);    }
        public void meta(String cat, String ver, String name, String desc)  { meta(cat, name, desc, ver, false); }
        public void metaD(String cat, String ver, String name, String desc) { meta(cat, name, desc, ver, true);  }

        private void meta(String cat, String name, String desc, String ver, boolean deprecated) {
            MetaData meta = new MetaData(name, desc, cat, vars, flags, ver, deprecated);
            for (String var : vars)
                VariableRegistry.metadata.put(var, meta);
        }
    }

    public static class DefaultCategories {
        public static final String PERFORMANCE = "Performance";
        public static final String WORLD_SERVER = "Server / World";
        public static final String MISC = "Misc";
        public static final String POSITION = "Position";
        public static final String DIRECTION = "Direction";
        public static final String MOVEMENT = "Movement";
        public static final String ENVIRONMENT = "Environment";
        public static final String ENTITIES = "Entities";
        public static final String DAY_TIME = "Day / Time";
        public static final String DIFF_LIGHT = "Difficulty / Light";
        public static final String CHUNKS = "Chunk Info";
        public static final String SOUNDS = "Sounds";
        public static final String TARGET = "Target";
        public static final String ITEMS = "Items";
        public static final String FISHING = "Fishing Rod";
        public static final String PC_INFO = "PC Info / Usage";
        public static final String TECHNICAL = "Technical";
        public static final String FROM_MODS = "From Mods";
        public static final String DEPRECATED = "Deprecated";
    }

    public static class DefaultAvailableFlags {
        public static final String NONE = "None";
        public static final String STRING = "String";
        public static final String NUMBER = "Precision and Scale";
        public static final String STAT = "Formatted";
        public static final String ICON = "Icon";
        public static final String ITEM_SLOT = "Item Icon";

        public static String fromEntryType(VariableRegistry.SupplierEntryType type) {
            return switch (type) {
                case BOOLEAN -> NONE;
                case STRING, SPECIAL -> STRING;
                case STR_INT, DEC, INT -> NUMBER;
            };
        }
    }



}
