package water;

import renderEngine.Loader;
import renderEngine.MasterRenderer;
import terrains.Terrain;

import java.util.ArrayList;
import java.util.List;

public class Water {

    private List<WaterTile> waterTiles;
    private WaterRenderer waterRenderer;
    private WaterFrameBuffers waterFrameBuffers;
    private WaterShader waterShader;

    public Water(Loader loader, MasterRenderer renderer, float waterLevel, float terrainSize) {
        this.waterFrameBuffers = new WaterFrameBuffers();
        this.waterShader = new WaterShader();
        this.waterRenderer = new WaterRenderer(loader, this.waterShader, renderer.getProjectionMatrix(), this.waterFrameBuffers);

        this.waterTiles = new ArrayList<>();
        WaterTile water = new WaterTile(terrainSize / 2, terrainSize / 2, waterLevel);
        this.waterTiles.add(water);
    }

    public List<WaterTile> getWaterTiles() {
        return waterTiles;
    }

    public WaterRenderer getWaterRenderer() {
        return waterRenderer;
    }

    public WaterFrameBuffers getWaterFrameBuffers() {
        return waterFrameBuffers;
    }

    public WaterShader getWaterShader() {
        return waterShader;
    }
}
