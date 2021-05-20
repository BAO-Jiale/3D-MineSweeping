import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;

public class GridComponent extends SimpleApplication {
    private int row;
    private int col;
    private Status status;
    public Geometry geometry;
    private int content = 0;

    public GridComponent(int x, int y) {
        this.row = x;
        this.col = y;
    }

    @Override
    public void simpleInitApp() {
        /* Initialize the game scene here */
        draw(this.geometry);
    }

    @Override
    public void simpleUpdate(float tpf) {
        /* Interact with game events in the main loop */
    }

    @Override
    public void simpleRender(RenderManager rm) {
        /* (optional) Make advanced modifications to frameBuffer and scene graph. */
    }

    public void draw(Spatial g){
        if (this.status==Status.Covered_with_Mine|this.status==Status.Covered_without_Mine){
            Spatial grass = assetManager.loadModel("main/resources/Models/Grass/Grass.obj");
            Material grassMat = assetManager.loadMaterial("main/resources/Models/Grass/Grass.mtl");
            grass.setMaterial(grassMat);
            g=grass;
        }
        if (this.status==Status.Mine){
            Spatial mine = assetManager.loadModel("main/resources/Models/Mine/Mine.j3o");
            g=mine;
        }
        if (this.status==Status.Flag){}
        if (this.status==Status.Clear){}

        g.scale(10.0f);
        g.center();
        float m=(float)(row);
        float n=(float)(col);
        g.setLocalTranslation(m,n,0.0f);

        // #2 创造光源

        // 定向光
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-1, -2, -3));

        // 环境光
        AmbientLight ambient = new AmbientLight();

        // 调整光照亮度
        ColorRGBA lightColor = new ColorRGBA();
        sun.setColor(lightColor.mult(0.6f));
        ambient.setColor(lightColor.mult(0.4f));

        // #3 将模型和光源添加到场景图中
        rootNode.attachChild(g);
        rootNode.addLight(sun);
        rootNode.addLight(ambient);
    }

    public void onPause(){
        if (MainFrame.gameController.isPaused){
        //继续计时，窗口消失


        MainFrame.gameController.isPaused=false;}
        if (!MainFrame.gameController.isPaused){
            //暂停计时，出现暂停窗口


            MainFrame.gameController.isPaused=true;}
    }
    public void onCheat(){
        if (this.status==Status.Covered_with_Mine){status=Status.Mine;}
        if (this.status==Status.Covered_without_Mine){status=Status.Clear;}
    }
    public void onOpen(){
        if (this.status==Status.Covered_with_Mine){status=Status.Mine;}
        if (this.status==Status.Covered_without_Mine){status=Status.Clear;}
        MainFrame.gameController.nextTurn();
    }
    public void onMark(){
        if (this.status==Status.Covered_with_Mine){status=Status.Flag;}
        if (this.status==Status.Covered_without_Mine){status=Status.Flag;}
        MainFrame.gameController.nextTurn();
    }
    public void onRestart(){
        if (this.status==Status.Mine){status=Status.Covered_with_Mine;}
        if (this.status==Status.Clear){status=Status.Covered_without_Mine;}
        if (this.status==Status.Flag){
            if (MainFrame.mineField.getMine()[row][col]==-1){status=Status.Covered_with_Mine;}
            else {status=Status.Covered_without_Mine;}
        }
    }


}
