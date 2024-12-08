package me.dmitrygubanov40.concan.utility;

import java.awt.Color;



/**
 * Narrow spectrum colors for console output.
 * Contains both 4 bit system universal colors, and all palette with 256 colors.
 * Also has greyscale palette.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public enum ConCol
{
    
    // 16 system colors:
    // (appropriate for 4bit color)
    BLACK       (0,     "Black",    new Color(0,   0,   0)      ),
    MAROON      (1,     "Maroon",   new Color(128, 0,   0)      ),
    GREEN       (2,     "Green",    new Color(0,   128, 0)      ),
    OLIVE       (3,     "Olive",    new Color(128, 128, 0)      ),
    NAVY        (4,     "Navy",     new Color(0,   0,   128)    ),
    PURPLE      (5,     "Purple",   new Color(128, 0,   128)    ),
    TEAL        (6,     "Teal",     new Color(0,   128, 128)    ),
    SILVER      (7,     "Silver",   new Color(192, 192, 192)    ),
    GREY        (8,     "Grey",     new Color(128, 128, 128)    ),
    RED         (9,     "Red",      new Color(255, 0,   0)      ),
    LIME        (10,    "Lime",     new Color(0,   255, 0)      ),
    YELLOW      (11,    "Yellow",   new Color(255, 255, 0)      ),
    BLUE        (12,    "Blue",     new Color(0,   0,   255)    ),
    FUCHSIA     (13,    "Fuchsia",  new Color(255, 0,   255)    ),
    AQUA        (14,    "Aqua",     new Color(0,   255, 255)    ),
    WHITE       (15,    "White",    new Color(255, 255, 255)    ),
    //
    // 1/6 of 36 colors block:
    // (blue-green gradients)
    BLACK1              (16, "Black1",              new Color(0,   0,   0)      ),
    ABYSSAL_BLUE        (17, "Abyssal Blue",        new Color(0,   0,   95)     ),
    MIDNIGHT_BLUE       (18, "Midnight Blue",       new Color(0,   0,   135)    ),
    BOHEMIAN_BLUE       (19, "Bohemian Blue",       new Color(0,   0,   175)    ),
    BLUE_ALICIOUS       (20, "Blue Alicious",       new Color(0,   0,   215)    ),
    BLUE1               (21, "Blue1",               new Color(0,   0,   255)    ),
    CUCUMBER            (22, "Cucumber",            new Color(0,   95,  0)      ),
    SANDHILL_CRANE      (23, "Sandhill Crane",      new Color(0,   95,  95)     ),
    BLUE_FLAME          (24, "Blue Flame",          new Color(0,   95,  135)    ),
    COBALT_STONE        (25, "Cobalt Stone",        new Color(0,   95,  175)    ),
    BLUE_RUIN           (26, "Blue Ruin",           new Color(0,   95,  215)    ),
    BRIGHT_BLUE         (27, "Bright Blue",         new Color(0,   95,  255)    ),
    FINE_PINE           (28, "Fine Pine",           new Color(0,   135, 0)      ),
    ABSINTHE_TURQUOISE  (29, "Absinthe Turquoise",  new Color(0,   135, 95)     ),
    GREEN_MOBLIN        (30, "Green Moblin",        new Color(0,   135, 135)    ),
    STOMY_SHOWER        (31, "Stomy Shower",        new Color(0,   135, 175)    ),
    BLUE_COLA           (32, "Blue Cola",           new Color(0,   135, 215)    ),
    DODGER_BLUE         (33, "Dodger Blue",         new Color(0,   135, 255)    ),
    PHOSPHOR_GREEN      (34, "Phosphor Green",      new Color(0,   175, 0)      ),
    SPRING_GREEN        (35, "Spring Green",        new Color(0,   175, 95)     ),
    ARCADIA             (36, "Arcadia",             new Color(0,   175, 135)    ),
    FIJI                (37, "Fiji",                new Color(0,   175, 175)    ),
    MALIBU_BLUE         (38, "Malibu Blue",         new Color(0,   175, 215)    ),
    KRISHNA_BLUE        (39, "Krishna Blue",        new Color(0,   175, 255)    ),
    GREEN_ALICIOUS      (40, "Green Alicious",      new Color(0,   215, 0)      ),
    ALIENATED           (41, "Alienated",           new Color(0,   215, 95)     ),
    UNDERWATER_FERN     (42, "Underwater Fern",     new Color(0,   215, 135)    ),
    MARKER_GREEN        (43, "Marker Green",        new Color(0,   215, 175)    ),
    JADE_GLASS          (44, "Jade Glass",          new Color(0,   215, 215)    ),
    NEON_BLUE           (45, "Neon Blue",           new Color(0,   215, 255)    ),
    LIME1               (46, "Lime1",               new Color(0,   255, 0)      ),
    CATHODE_GREEN       (47, "Cathode Green",       new Color(0,   255, 95)     ),
    GUPPIE_GREEN        (48, "Guppie Green",        new Color(0,   255, 135)    ),
    GREENISH_TURQUOISE  (49, "Greenish Turquoise",  new Color(0,   255, 175)    ),
    ICE                 (50, "Ice",                 new Color(0,   255, 215)    ),
    AQUA1               (51, "Aqua1",               new Color(0,   255, 255)    ),
    //
    // 2/6 of 36 colors block:
    SPIKEY_RED          (52, "Spikey Red",          new Color(95,  0,   0)      ),
    CLEAR_PLUM          (53, "Clear Plum",          new Color(95,  0,   95)     ),
    PEACEFUL_PURPLE     (54, "Peaceful Purple",     new Color(95,  0,   135)    ),
    AUBERGINE_PERL      (55, "Aubergine Perl",      new Color(95,  0,   175)    ),
    TRUSTED_PURPLE      (56, "Trusted Purple",      new Color(95,  0,   215)    ),
    ELECTRIC_INDIGO     (57, "Electric Indigo",     new Color(95,  0,   255)    ),
    MUD_GREEN           (58, "Mud Green",           new Color(95,  95,  0)      ),
    RHINE_CASTLE        (59, "Rhine Castle",        new Color(95,  95,  95)     ),
    PURPLE_BALLOON      (60, "Purple Balloon",      new Color(95,  95,  135)    ),
    BELLFLOWER          (61, "Bellflower",          new Color(95,  95,  175)    ),
    EXODUS_FRUIT        (62, "Exodus Fruit",        new Color(95,  95,  215)    ),
    BLUE_GENIE          (63, "Blue Genie",          new Color(95,  95,  255)    ),
    PESTO_ALLA          (64, "Pesto Alla",          new Color(95,  135, 0)      ),
    HIPPIE_GREEN        (65, "Hippie Green",        new Color(95,  135, 95)     ),
    STEEL_TEAL          (66, "Steel Teal",          new Color(95,  135, 135)    ),
    PACIFIC_COAST       (67, "Pacific Coast",       new Color(95,  135, 175)    ),
    BLUE_JAY            (68, "Blue Jay",            new Color(95,  135, 215)    ),
    DEEP_DENIM          (69, "Deep Denim",          new Color(95,  135, 255)    ),
    KERMIT_GREEN        (70, "Kermit Green",        new Color(95,  175, 0)      ),
    BORING_GREEN        (71, "Boring Green",        new Color(95,  175, 95)     ),
    VERDIGRIS_GREEN     (72, "Verdigris Green",     new Color(95,  175, 135)    ),
    AQUARELLE           (73, "Aquarelle",           new Color(95,  175, 175)    ),
    FLYWAY              (74, "Flyway",              new Color(95,  175, 215)    ),
    STEEL_BLUE          (75, "Steel Blue",          new Color(95,  175, 255)    ),
    RADIOACTIVE_LILYPAD (76, "Radioactive Lilypad", new Color(95,  215, 0)      ),
    LIGHTISH_GREEN      (77, "Lightish Green",      new Color(95,  215, 95)     ),
    SPRING_BOUQUET      (78, "Spring Bouquet",      new Color(95,  215, 135)    ),
    MEDIUM_AQUAMARINE   (79, "Medium Aquamarine",   new Color(95,  215, 175)    ),
    HAMMAM_BLUE         (80, "Hammam Blue",         new Color(95,  215, 215)    ),
    ATHENA_BLUE         (81, "Athena Blue",         new Color(95,  215, 255)    ),
    BRIGHT_GREEN        (82, "Bright Green",        new Color(95,  255, 0)      ),
    SCREAMING_GREEN     (83, "Screaming Green",     new Color(95,  255, 95)     ),
    THALLIUM_FLAME      (84, "Thallium Flame",      new Color(95,  255, 135)    ),
    INEFFABLE_GREEN     (85, "Ineffable Green",     new Color(95,  255, 175)    ),
    RARE_WIND           (86, "Rare Wind",           new Color(95,  255, 215)    ),
    MOONGLADE_WATER     (87, "Moonglade Water",     new Color(95,  255, 255)    ),
    //
    // 3/6 of 36 colors block:
    CHANTICLEER         (88,    "Chanticleer",      new Color(135, 0,   0)      ),
    GRAPEST             (89,    "Grapest",          new Color(135, 0,   95)     ),
    MARDI_GRAS          (90,    "Mardi Gras",       new Color(135, 0,   135)    ),
    DARK_MAGENTA        (91,    "Dark Magenta",     new Color(135, 0,   175)    ),
    FRENCH_VIOLET       (92,    "French Violet",    new Color(135, 0,   215)    ),
    PURPLE_CLIMAX       (93,    "Purple Climax",    new Color(135, 0,   255)    ),
    RAT_BROWN           (94,    "Rat Brown",        new Color(135, 95,  0)      ),
    RABBIT_PAWS         (95,    "Rabbit Paws",      new Color(135, 95,  95)     ),
    CANDY_VIOLET        (96,    "Candy Violet",     new Color(135, 95,  135)    ),
    LUSTY_LAVENDER      (97,    "Lusty Lavender",   new Color(135, 95,  175)    ),
    GLOOMY_PURPLE       (98,    "Gloomy Purple",    new Color(135, 95,  215)    ),
    PURPLE_ANEMONE      (99,    "Purple Anemone",   new Color(135, 95,  255)    ),
    DRABLY_OLIVE        (100,   "Drably Olive",     new Color(135, 135, 0)      ),
    WHEAT               (101,   "Wheat",            new Color(135, 135, 95)     ),
    MITHRIL             (102,   "Mithril",          new Color(135, 135, 135)    ),
    ASTER_PURPLE        (103,   "Aster Purple",     new Color(135, 135, 175)    ),
    TANZINE             (104,   "Tanzine",          new Color(135, 135, 215)    ),
    SALT_BLUE           (105,   "Salt Blue",        new Color(135, 135, 255)    ),
    FRESH_LAWN          (106,   "Fresh Lawn",       new Color(135, 175, 0)      ),
    BROCCOLI            (107,   "Broccoli",         new Color(135, 175, 95)     ),
    CHATTY_CRICKET      (108,   "Chatty Cricket",   new Color(135, 175, 135)    ),
    HYDROLOGY           (109,   "Hydrology",        new Color(135, 175, 175)    ),
    BLUE_BELL           (110,   "Blue Bell",        new Color(135, 175, 215)    ),
    KITTEN_EYE          (111,   "Kitten Eye",       new Color(135, 175, 255)    ),
    OVERGROWN           (112,   "Overgrown",        new Color(135, 215, 0)      ),
    LILLIPUTIAN_LIME    (113,   "Lilliputian Lime", new Color(135, 215, 95)     ),
    GREEK_GARDEN        (114,   "Greek Garden",     new Color(135, 215, 135)    ),
    JOVIAL_JADE         (115,   "Jovial Jade",      new Color(135, 215, 175)    ),
    ISLAND_OASIS        (116,   "Island Oasis",     new Color(135, 215, 215)    ),
    TRANQUIL_POOL       (117,   "Tranquil Pool",    new Color(135, 215, 255)    ),
    LASTING_LIME        (118,   "Lasting Lime",     new Color(135, 255, 0)      ),
    POISONOUS_DART      (119,   "Poisonous Dart",   new Color(135, 255, 95)     ),
    EASTER_GREEN        (120,   "Easter Green",     new Color(135, 255, 135)    ),
    PALE_GREEN          (121,   "Pale Green",       new Color(135, 255, 175)    ),
    TIBETAN_PLATEAU     (122,   "Tibetan Plateau",  new Color(135, 255, 215)    ),
    GLITTER_SHOWER      (123,   "Glitter Shower",   new Color(135, 255, 255)    ),
    //
    // 4/6 of 36 colors block:
    RED_DOOR            (124,   "Red Door",         new Color(175, 0,   0)      ),
    VELVET_CUPCAKE      (125,   "Velvet Cupcake",   new Color(175, 0,   95)     ),
    VIBRANT_VELVET      (126,   "Vibrant Velvet",   new Color(175, 0,   135)    ),
    ENERGIC_EGGPLANT    (127,   "Energic Eggplant", new Color(175, 0,   175)    ),
    VIBRANT_PURPLE      (128,   "Vibrant Purple",   new Color(175, 0,   215)    ),
    PURPLE_PARADISE     (129,   "Purple Paradise",  new Color(175, 0,   255)    ),
    ORANGE_BROWN        (130,   "Orange Brown",     new Color(175, 95,  0)      ),
    ITALIAN_VILLA       (131,   "Italian Villa",    new Color(175, 95,  95)     ),
    DAHLIA_MAUVE        (132,   "Dahlia Mauve",     new Color(175, 95,  135)    ),
    MEDIUM_ORCHID       (133,   "Medium Orchid",    new Color(175, 95,  175)    ),
    TELDRASSIL_PURPLE   (134,   "Teldrassil Purple",new Color(175, 95,  215)    ),
    PURPLE_HEDONIST     (135,   "Purple Hedonist",  new Color(175, 95,  255)    ),
    STRONG_MUSTARD      (136,   "Strong Mustard",   new Color(175, 135, 0)      ),
    CLAY_OCHRE          (137,   "Clay Ochre",       new Color(175, 135, 95)     ),
    WOODROSE            (138,   "Woodrose",         new Color(175, 135, 135)    ),
    DUSTY_LAVENDER      (139,   "Dusty Lavender",   new Color(175, 135, 175)    ),
    MEDIUM_PURPLE       (140,   "Medium Purple",    new Color(175, 135, 215)    ),
    LILAC_GEODE         (141,   "Lilac Geode",      new Color(175, 135, 255)    ),
    CITRUS              (142,   "Citrus",           new Color(175, 175, 0)      ),
    PALM                (143,   "Palm",             new Color(175, 175, 95)     ),
    NAVAJO_WHITE        (144,   "Navajo White",     new Color(175, 175, 135)    ),
    SMOKE_SCREEN        (145,   "Smoke Screen",     new Color(175, 175, 175)    ),
    PIXIE_VIOLET        (146,   "Pixie Violet",     new Color(175, 175, 215)    ),
    LAVENDER_BLUE       (147,   "Lavender Blue",    new Color(175, 175, 255)    ),
    KING_LIME           (148,   "King Lime",        new Color(175, 215, 0)      ),
    LIME_LIZARD         (149,   "Lime Lizard",      new Color(175, 215, 95)     ),
    FRESH_LETTUCE       (150,   "Fresh Lettuce",    new Color(175, 215, 135)    ),
    FLOWER_STEM         (151,   "Flower Stem",      new Color(175, 215, 175)    ),
    RIVERS_EDGE         (152,   "Rivers Edge",      new Color(175, 215, 215)    ),
    COLD_STARE          (153,   "Cold Stare",       new Color(175, 215, 255)    ),
    LIME_ACID           (154,   "Lime Acid",        new Color(175, 255, 0)      ),
    OLIVE_GREEN         (155,   "Olive Green",      new Color(175, 255, 95)     ),
    PISTACHIO_MOUSSE    (156,   "Pistachio Mousse", new Color(175, 255, 135)    ),
    CREAMY_MINT         (157,   "Creamy Mint",      new Color(175, 255, 175)    ),
    MINTASTIC           (158,   "Mintastic",        new Color(175, 255, 215)    ),
    CELESTE             (159,   "Celeste",          new Color(175, 255, 255)    ),
    //
    // 5/6 of 36 colors block:
    RED_REPUBLIC        (160,   "Red Republic",     new Color(215, 0,   0)      ),
    DEEP_PINK           (161,   "Deep Pink",        new Color(215, 0,   95)     ),
    HOLLYWOOD_CERISE    (162,   "Hollywood Cerise", new Color(215, 0,   135)    ),
    DEEP_MAGENTA        (163,   "Deep Magenta",     new Color(215, 0,   175)    ),
    FUCHSIA_INTENSO     (164,   "Fuchsia Intenso",  new Color(215, 0,   215)    ),
    HOT_PURPLE          (165,   "Hot Purple",       new Color(215, 0,   255)    ),
    EXUBERANCE          (166,   "Exuberance",       new Color(215, 95,  0)      ),
    ROMAN               (167,   "Roman",            new Color(215, 95,  95)     ),
    SURFER_GIRL         (168,   "Surfer Girl",      new Color(215, 95,  135)    ),
    MEGA_MAGENTA        (169,   "Mega Magenta",     new Color(215, 95,  175)    ),
    ORCHID              (170,   "Orchid",           new Color(215, 95,  215)    ),
    FLAMINGO            (171,   "Flamingo",         new Color(215, 95,  255)    ),
    MANGO               (172,   "Mango",            new Color(215, 135, 0)      ),
    BRIGHT_SIENNA       (173,   "Bright Sienna",    new Color(215, 135, 95)     ),
    PEACH               (174,   "Peach",            new Color(215, 135, 135)    ),
    SPRINGTIME_BLOOM    (175,   "Springtime Bloom", new Color(215, 135, 175)    ),
    LAVENDER_PINK       (176,   "Lavender Pink",    new Color(215, 135, 215)    ),
    LAVENDER_TEA        (177,   "Lavender Tea",     new Color(215, 135, 255)    ),
    PALOMINO_GOLD       (178,   "Palomino Gold",    new Color(215, 175, 0)      ),
    SELL_GOLD           (179,   "Sell Gold",        new Color(215, 175, 95)     ),
    PORCINI             (180,   "Porcini",          new Color(215, 175, 135)    ),
    MARY_ROSE           (181,   "Mary Rose",        new Color(215, 175, 175)    ),
    BLUSHING_SKY        (182,   "Blushing Sky",     new Color(215, 175, 215)    ),
    LIGHT_VIOLET        (183,   "Light Violet",     new Color(215, 175, 255)    ),
    CHARTREUSE_SHOT     (184,   "Chartreuse Shot",  new Color(215, 215, 0)      ),
    BANANA_CHALK        (185,   "Banana Chalk",     new Color(215, 215, 95)     ),
    WAX_GREEN           (186,   "Wax Green",        new Color(215, 215, 135)    ),
    GREEN_MESH          (187,   "Green Mesh",       new Color(215, 215, 175)    ),
    CAPE_HOPE           (188,   "Cape Hope",        new Color(215, 215, 215)    ),
    TRANSPARENT_BLUE    (189,   "Transparent Blue", new Color(215, 215, 255)    ),
    LIME_ZEST           (190,   "Lime Zest",        new Color(215, 255, 0)      ),
    ISOTONIC_WATER      (191,   "Isotonic Water",   new Color(215, 255, 95)     ),
    GREEN_SHIMMER       (192,   "Green Shimmer",    new Color(215, 255, 135)    ),
    LIME_MIST           (193,   "Lime Mist",        new Color(215, 255, 175)    ),
    TRANSPARENT_GREEN   (194,   "Transparent Green",new Color(215, 255, 215)    ),
    REFRESHING_PRIMER   (195,   "Refreshing Primer",new Color(215, 255, 255)    ),
    //
    // 6/6 of 36 colors block:
    RED1                (196,   "Red1",             new Color(255, 0,   0)      ),
    RAZZMATAZZ          (197,   "Razzmatazz",       new Color(255, 0,   95)     ),
    FANCY_FUCHSIA       (198,   "Fancy Fuchsia",    new Color(255, 0,   135)    ),
    BRIGHT_PINK         (199,   "Bright Pink",      new Color(255, 0,   175)    ),
    FUCHSIA_FLAME       (200,   "Fuchsia Flame",    new Color(255, 0,   215)    ),
    FUCHSIA1            (201,   "Fuchsia1",         new Color(255, 0,   255)    ),
    VIVID_ORANGE        (202,   "Vivid Orange",     new Color(255, 95,  0)      ),
    FUSION_RED          (203,   "Fusion Red",       new Color(255, 95,  95)     ),
    WATERMELON          (204,   "Watermelon",       new Color(255, 95,  135)    ),
    HOT_PINK            (205,   "Hot Pink",         new Color(255, 95,  175)    ),
    ILLICIT_PINK        (206,   "Illicit Pink",     new Color(255, 95,  215)    ),
    VIOLET_PINK         (207,   "Violet Pink",      new Color(255, 95,  255)    ),
    MANDARIN_JELLY      (208,   "Mandarin Jelly",   new Color(255, 135, 0)      ),
    CORAL               (209,   "Coral",            new Color(255, 135, 95)     ),
    RED_MULL            (210,   "Red Mull",         new Color(255, 135, 135)    ),
    PALE_RED            (211,   "Pale Red",         new Color(255, 135, 175)    ),
    PINK_DELIGHT        (212,   "Pink Delight",     new Color(255, 135, 215)    ),
    DARLING_BUD         (213,   "Darling Bud",      new Color(255, 135, 255)    ),
    FRESH_SQUEEZED      (214,   "Fresh Squeezed",   new Color(255, 175, 0)      ),
    VINTAGE_ORANGE      (215,   "Vintage Orange",   new Color(255, 175, 95)     ),
    SPICE_PINK          (216,   "Spice Pink",       new Color(255, 175, 135)    ),
    MELON               (217,   "Melon",            new Color(255, 175, 175)    ),
    LAVENDER_CANDY      (218,   "Lavender Candy",   new Color(255, 175, 215)    ),
    LAVENDER_ROSE       (219,   "Lavender Rose",    new Color(255, 175, 255)    ),
    GOLD                (220,   "Gold",             new Color(255, 215, 0)      ),
    DANDELION           (221,   "Dandelion",        new Color(255, 215, 95)     ),
    SALOMIE             (222,   "Salomie",          new Color(255, 215, 135)    ),
    SANDY_TAN           (223,   "Sandy Tan",        new Color(255, 215, 175)    ),
    MISTY_ROSE          (224,   "Misty Rose",       new Color(255, 215, 215)    ),
    SUGARPILLS          (225,   "Sugarpills",       new Color(255, 215, 255)    ),
    YELLOW1             (226,   "Yellow1",          new Color(255, 255, 0)      ),
    CANARY              (227,   "Canary",           new Color(255, 255, 95)     ),
    KHAKI               (228,   "Khaki",            new Color(255, 255, 135)    ),
    SHALIMAR            (229,   "Shalimar",         new Color(255, 255, 175)    ),
    CREAM               (230,   "Cream",            new Color(255, 255, 215)    ),
    WHITE1              (231,   "White1",           new Color(255, 255, 255)    ),
    //
    // 24 grayscale colors:
    GREYSCALE1  (232, "Greyscale1",     new Color(8,   8,   8)      ),
    GREYSCALE2  (233, "Greyscale2",     new Color(18,  18,  18)     ),
    GREYSCALE3  (234, "Greyscale3",     new Color(28,  28,  28)     ),
    GREYSCALE4  (235, "Greyscale4",     new Color(38,  38,  38)     ),
    GREYSCALE5  (236, "Greyscale5",     new Color(48,  48,  48)     ),
    GREYSCALE6  (237, "Greyscale6",     new Color(58,  58,  58)     ),
    GREYSCALE7  (238, "Greyscale7",     new Color(68,  68,  68)     ),
    GREYSCALE8  (239, "Greyscale8",     new Color(78,  78,  78)     ),
    GREYSCALE9  (240, "Greyscale9",     new Color(88,  88,  88)     ),
    GREYSCALE10 (241, "Greyscale10",    new Color(98,  98,  98)     ),
    GREYSCALE11 (242, "Greyscale11",    new Color(108, 108, 108)    ),
    GREYSCALE12 (243, "Greyscale12",    new Color(118, 118, 118)    ),
    GREYSCALE13 (244, "Greyscale13",    new Color(128, 128, 128)    ),
    GREYSCALE14 (245, "Greyscale14",    new Color(138, 138, 138)    ),
    GREYSCALE15 (246, "Greyscale15",    new Color(148, 148, 148)    ),
    GREYSCALE16 (247, "Greyscale16",    new Color(158, 158, 158)    ),
    GREYSCALE17 (248, "Greyscale17",    new Color(168, 168, 168)    ),
    GREYSCALE18 (249, "Greyscale18",    new Color(178, 178, 178)    ),
    GREYSCALE19 (250, "Greyscale19",    new Color(188, 188, 188)    ),
    GREYSCALE20 (251, "Greyscale20",    new Color(198, 198, 198)    ),
    GREYSCALE21 (252, "Greyscale21",    new Color(208, 208, 208)    ),
    GREYSCALE22 (253, "Greyscale22",    new Color(218, 218, 218)    ),
    GREYSCALE23 (254, "Greyscale23",    new Color(228, 228, 228)    ),
    GREYSCALE24 (255, "Greyscale24",    new Color(238, 238, 238)    );
    
    ////////////////////////////
    
    // only this number of colors is allowed for 4 bit color regime
    private static final int LIMIT_4B;
    // color code shift for the first 8 colors in 4 bit regime
    private static final int FIRST_COLORS_SHIFT_4B;
    // color code shift for the last 8 colors in 4 bit regime
    private static final int LAST_COLORS_SHIFT_4B;
    // background color code has this shift in comparison with regular code
    private static final int BACKGROUND_SHIFT_4B;
    
    
    // ConsoleColor-array with grey spectrum from the darkest grey to the lightest one
    // includes black and white colors as part of spectrum
    private static final ConCol[] GREYSCALE;
    
    
    // two (x2) delta^2 between black and white colors (to start search)
    private static final double MAX_RGB_COLOR_DELTA_POWED2;
    
    // enought color delta^2 to consider color is the same
    private static final double MIN_RGB_COLOR_DELTA_POWED2;
    
    
    static {
        LIMIT_4B = 16;
        FIRST_COLORS_SHIFT_4B = 30;
        LAST_COLORS_SHIFT_4B = 82;
        BACKGROUND_SHIFT_4B = 10;
        //
        // 24 + 2 = 26 colors of grey scale
        GREYSCALE = new ConCol[] {
                        BLACK,
                        GREYSCALE1,  GREYSCALE2,  GREYSCALE3,  GREYSCALE4,  GREYSCALE5,  GREYSCALE6,
                        GREYSCALE7,  GREYSCALE8,  GREYSCALE9,  GREYSCALE10, GREYSCALE11, GREYSCALE12,
                        GREYSCALE13, GREYSCALE14, GREYSCALE15, GREYSCALE16, GREYSCALE17, GREYSCALE18,
                        GREYSCALE19, GREYSCALE20, GREYSCALE21, GREYSCALE22, GREYSCALE23, GREYSCALE24,
                        WHITE };
        //
        MAX_RGB_COLOR_DELTA_POWED2 = 2 * (3 * 255*255);
        MIN_RGB_COLOR_DELTA_POWED2 = 55 * 55;// calculated experimentally
    }
    
    
    private final String name;
    private final int colorCode;
    private final Color trueColor;
    
    ////////////////////////////
    
    /**
     * @param initColor color code (0..255)
     * @param initName text name of color
     * @param initTrueColor real color system (java.awt.Color)
     */
    ConCol(final int initColor, final String initName, final Color initTrueColor) {
        this.name = initName;
        this.colorCode = initColor;
        this.trueColor = initTrueColor;
    }
    
    /**
     * @return color code of the console color
     */
    public int getColorCode() {
        return this.colorCode;
    }
    
    /**
     * @return color text name
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * @return access for 'java.awt.Color'-object
     */
    public Color getTrueColor() {
        return this.trueColor;
    }
    
    /**
     * @return 'true' for short system 4-bit color
     */
    private boolean is4bit() {
        boolean result = (this.colorCode < ConCol.LIMIT_4B);
        return result;
    }
    
    /**
     * Is the color de-facto from greyscale palette?
     * @return 'true' if from greyscale palette
     */
    private boolean isGreyScale() {
        boolean result = false;
        //
        for ( ConCol curColor : GREYSCALE ) {
            if ( this.trueColor.equals(curColor.getTrueColor()) ) {
                result = true;
                break;
            }
        }
        //
        return result;
    }
    
    /**
     * Get safely color from grey spectrum via its index.
     * @param index index of lightness (more index - lighter the color)
     * @return ConsoleColor element of that index of lightness, or black/white color for out-of-boundaries
     */
    public static ConCol getGreyscaleColor(final int index) {
        if ( index < 0 ) {
            return GREYSCALE[0];
        }
        if ( index >= GREYSCALE.length ) {
            return GREYSCALE[ GREYSCALE.length - 1 ];
        }
        //
        return GREYSCALE[ index ];
    }
    
    /**
     * Translate system color code for console purposes.
     * @param isBackground will the color be used as background?
     * @return 4-bit color code for escape sequence
     */
    private int getColorVGA(final boolean isBackground) throws IllegalArgumentException {
        if ( !this.is4bit() ) {
            String excMsg = "Illegal VGA color, color code: " + this.getColorCode();
            throw new IllegalArgumentException(excMsg);
        }
        //
        int consoleColorCodeVGA = this.getColorCode();
        //
        if ( this.getColorCode() <= (LIMIT_4B / 2) ) {
            // first 8 colors shif
            consoleColorCodeVGA += FIRST_COLORS_SHIFT_4B;
        } else {
            // last 8 colors shift
            consoleColorCodeVGA += LAST_COLORS_SHIFT_4B;
        }
        //
        if ( isBackground ) {
            // special background shift
            consoleColorCodeVGA += BACKGROUND_SHIFT_4B;
        }
        //
        return consoleColorCodeVGA;
    }
    public int getColorCodeVGA() {
        return this.getColorVGA(false);
    }
    public int getBackgroundCodeVGA() {
        return this.getColorVGA(true);
    }
    
    
    
    /**
     * 
     * @param colorRgb random true color we search an analog in 'ConCol'
     * @return the most close visually 'ConCol'-color (via chosen method)
     */
    public static ConCol getAnalog(final Color colorRgb) {
        // default values to start:
        ConCol result = ConCol.BLACK;   // we will update and return this
        double minDelta = MAX_RGB_COLOR_DELTA_POWED2;
        //
        ConCol[] allConsoleColors = ConCol.class.getEnumConstants();// get all our colors
        for ( ConCol currentConCol : allConsoleColors ) {
            //
            double currentDelta = colorDeltaPowed2(colorRgb, currentConCol.getTrueColor());
            //
            if ( 0 == currentDelta) {
                // it is the final answer - the same color
                result = currentConCol;
                break;
            }
            //
            if ( currentDelta < minDelta ) {
                // got a bit more close color in our enum list
                minDelta = currentDelta;
                result = currentConCol;
                // the color is close enough to be "the same"
                if ( minDelta < MIN_RGB_COLOR_DELTA_POWED2 ) break;
            }
        }
        //
        return result;
    }
    
    /**
     * Calculates colors delta^2 by "redmean" method.
     * https://en.wikipedia.org/wiki/Color_difference
     * @param color1
     * @param color2
     * @return RGB-color delta
     */
    private static double colorDeltaPowed2(final Color color1, final Color color2) {
        double result;
        //
        double mediumRed    = 0.5 * (color1.getRed() + color2.getRed());
        //
        double deltaRed     = color1.getRed()   - color2.getRed();
        double deltaGreen   = color1.getGreen() - color2.getGreen();
        double deltaBlue    = color1.getBlue()  - color2.getBlue();
        //
        result = (2 + mediumRed / 256) * deltaRed * deltaRed
                + 4 * deltaGreen * deltaGreen
                + (2 + (255 - mediumRed) / 256) * deltaBlue * deltaBlue;
        //
        return result;
    }
    
    
    
    @Override
    public String toString() {
        String greyscale = this.isGreyScale() ? "[greyscale]" : "";
        String system = this.is4bit() ? "[system 4b]" : "";
        String bit8 = ", 8b-color code: " + this.colorCode;
        String rgb = "rgb("
                        + this.trueColor.getRed() + ", "
                        + this.trueColor.getGreen() + ", "
                        + this.trueColor.getBlue() + ")";
        //
        String str = system + greyscale + this.name + bit8 + ", " + rgb;
        return str;
    }
    
    
    
}
