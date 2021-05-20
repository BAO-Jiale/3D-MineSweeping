import com.jme3.app.SimpleApplication;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.lwjgl.LwjglKeyInput;
import com.jme3.input.lwjgl.LwjglMouseInput;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.system.lwjgl.LwjglAbstractDisplay;
import org.lwjgl.LWJGLException;

public class MainFrame extends SimpleApplication {
    public static GameController gameController=new GameController(new Player(),new Player());
    EventListener listener=new EventListener();
    public static MineGenerator mineField=new MineGenerator("Junior");
    public GridComponent[][]temp=new GridComponent[mineField.getRow()][mineField.getCol()];
    public Status [][]status=mineField.getMineField();


    @Override
    public void simpleInitApp() {
        /* Initialize the game scene here */
        initKeys();
        flyCam.setEnabled(true);

        Spatial grass = assetManager.loadModel("main/resources/Models/Grass/Grass.blend");
        grass.scale(2.0f);
        grass.center();
        rootNode.attachChild(grass);
        inputManager.addListener(listener);
        for (GridComponent [] e:temp){
            for (GridComponent a:e){
            a.draw(a.geometry);
            rootNode.attachChild(a.geometry);
                // 环境光
                AmbientLight ambient = new AmbientLight();
                // 调整光照亮度
                ColorRGBA lightColor = new ColorRGBA();
                ambient.setColor(lightColor.mult(0.4f));
                // #3 将模型和光源添加到场景图中
                rootNode.addLight(ambient);
            }
        }

        // 添加灯光
        DirectionalLight sun = new DirectionalLight();
        sun.setColor(new ColorRGBA(0.7f, 0.7f, 0.7f, 1f));
        sun.setDirection(new Vector3f(-3, -4, -5).normalizeLocal());
        rootNode.addLight(sun);

        AmbientLight ambient = new AmbientLight();
        ambient.setColor(new ColorRGBA(0.2f, 0.2f, 0.2f, 1f));
        rootNode.addLight(ambient);








    }

    @Override
    public void simpleUpdate(float tpf) {
        /* Interact with game events in the main loop */
    }

    @Override
    public void simpleRender(RenderManager rm) {
        /* (optional) Make advanced modifications to frameBuffer and scene graph. */
    }

    LwjglAbstractDisplay e=new LwjglAbstractDisplay() {
        @Override
        public Type getType() {
            return null;
        }

        @Override
        public void setTitle(String s) {

        }

        @Override
        public void restart() {

        }

        @Override
        protected void createContext(AppSettings appSettings) throws LWJGLException {

        }

        @Override
        protected void destroyContext() {

        }

        @Override
        public void create(boolean b) {

        }
    };
    InputManager inputManager=new InputManager(new LwjglMouseInput(e),new LwjglKeyInput(e),null,null);
    public void initKeys(){
        // 你可以给一个或多个事件指定同样的事件名称
        inputManager.addMapping("Pause", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("Cheat", new KeyTrigger(KeyInput.KEY_T));
        inputManager.addMapping("Open", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("Mark", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));


    }



}