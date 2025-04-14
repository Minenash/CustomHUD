package com.minenash.customhud.v5.elements;

import static com.minenash.customhud.v5.ElementHelpers.*;
import static com.minenash.customhud.v5.ElementRegistry.*;

public class PositionElements {

    public static void load() {
        register( "x", 3, () -> camera().getX() );
        register( "y", 3, () -> camera().getY() );
        register( "z", 3, () -> camera().getZ() );

        register( n("nx", "nether_x"), () -> inNether() ?  camera().getX() * 8 : camera().getX() / 8 );
        register( n("nz", "nether_z"), () -> inNether() ?  camera().getZ() * 8 : camera().getZ() / 8 );

        register( n("bx", "block_x"), () -> camera().getBlockX() );
        register( n("by", "block_y"), () -> camera().getBlockY() );
        register( n("bz", "block_z"), () -> camera().getBlockZ() );

        register( n("icx", "in_chunk_x"), () ->  blockPos().getX() & 15 );
        register( n("icy", "in_chunk_y"), () ->  blockPos().getY() & 15 );
        register( n("icz", "in_chunk_z"), () ->  blockPos().getZ() & 15 );

        register( n("cx", "chunk_x"), () ->  blockPos().getX() >> 4 );
        register( n("cy", "chunk_y"), () ->  blockPos().getY() >> 4 );
        register( n("cz", "chunk_z"), () ->  blockPos().getZ() >> 4 );

        register( n("rex", "region_x"), () ->  blockPos().getX() >> 9 );
        register( n("rez", "region_z"), () ->  blockPos().getY() >> 9 );

        register( n("rrx", "region_relative_x"), () ->  blockPos().getX() >> 4 & 0x1F );
        register( n("rrz", "region_relative_z"), () ->  blockPos().getY() >> 4 & 0x1F );

    }

}
