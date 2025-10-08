package com.kakarot.util;

import com.kakarot.Main;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockType;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldedit.world.World;
import org.bukkit.Location;

import java.io.File;
import java.util.logging.Level;

public class WorldEditUtils {
    public static void setRegionToAir(Location corner1, Location corner2) {
        if(corner1.getWorld() != corner2.getWorld()) {
            Main.getInstance().getLogger().severe("WorldEdit error: setRegionToAir corners are in different worlds");
            return;
        }
        World worldEditWorld = BukkitUtil.getLocalWorld(corner1.getWorld());
        Vector pos1 = BukkitUtil.toVector(corner1);
        Vector pos2 = BukkitUtil.toVector(corner2);
        CuboidRegion region = new CuboidRegion(worldEditWorld, pos1, pos2);
        try {
            EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(worldEditWorld, -1);
            BaseBlock block = new BaseBlock(BlockType.AIR.getID());
            editSession.setBlocks(region, block);
            editSession.flushQueue();
        }catch (MaxChangedBlocksException e) {
            Main.getInstance().getLogger().log(Level.SEVERE, "An error occurred during a WorldEdit operation.", e);
        }
    }
    public static void pasteSchematic(File schematicFile, Location location, boolean noAir) {
        if(!schematicFile.exists()) {
            Main.getInstance().getLogger().severe("Schematic file not found: " + schematicFile.getPath());
            return;
        }
        World worldEditWorld = BukkitUtil.getLocalWorld(location.getWorld());
        Vector pasteLocation = BukkitUtil.toVector(location);
        try {
            SchematicFormat format = SchematicFormat.getFormat(schematicFile);
            if(format == null) {
                Main.getInstance().getLogger().severe("Unknown schematic format: " + schematicFile.getName());
                return;
            }
            EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(worldEditWorld, -1);
            format.load(schematicFile).paste(editSession, pasteLocation, noAir);
            editSession.flushQueue();
        }catch(Exception e) {
            Main.getInstance().getLogger().log(Level.SEVERE, "An error occurred during schematic pasting.", e);
        }
    }
}
