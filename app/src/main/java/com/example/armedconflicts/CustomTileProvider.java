package com.example.armedconflicts;

import android.content.res.AssetManager;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CustomTileProvider implements TileProvider {

    private static final int TILE_WIDTH = 256;
    private static final int TILE_HEIGHT = 256;
    private static final int BUFFER_SIZE = 16 * 1024;

    private AssetManager assets;

    public CustomTileProvider(AssetManager assets_) {
        assets = assets_;
    }

    @Override
    public Tile getTile(int x, int y, int zoom) {

        if (hasTile(x, y, zoom)) {
            byte[] image = readTileImage(x, y, zoom);
            return image == null ? null : new Tile(TILE_WIDTH, TILE_HEIGHT, image);
        }
        else
            return NO_TILE;

    }

    private byte[] readTileImage(int x, int y, int zoom) {
        InputStream in = null;
        ByteArrayOutputStream buffer = null;

        try {
            in = assets.open(getTileFilename(x, y, zoom));
            buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[BUFFER_SIZE];

            while ((nRead = in.read(data, 0, BUFFER_SIZE)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();

            return buffer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getTileFilename(int x, int y, int zoom) {
        //return "map/" + zoom + '/' + x + '/' + y + ".png";
        return "" + zoom + "" + x + "" + y + ".jpg";
    }

    private boolean hasTile(int x, int y, int zoom) {
        if ((zoom < 2 || zoom > 4)) {
            return false;
        }
        else if ((zoom == 4) && x >=7 && x <=9 && y >= 6 && y <=9)
            return true;
        return true;
    }

}
