package ch.idsia.mario.engine;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.human.CheaterKeyboardAgent;
import ch.idsia.evolve.ChallengeNNHandler;
import ch.idsia.evolve.ChallengeRecorder;
import ch.idsia.evolve.FrusNNHandler;
import ch.idsia.evolve.FrusRecorder;
import ch.idsia.evolve.FunRecorder;
import ch.idsia.evolve.GameType;
import ch.idsia.evolve.LevelSceneCustom;
import ch.idsia.evolve.NNHandler;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.GameViewer;
import ch.idsia.tools.tcp.ServerAgent;

import javax.swing.*;


import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.image.VolatileImage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MarioComponent extends JComponent implements Runnable, /*KeyListener,*/ FocusListener, Environment {
    private static final long serialVersionUID = 790878775993203817L;
    public static final int TICKS_PER_SECOND = 24;
    private NNHandler nnHandler;
    private FrusNNHandler frusnnHandler;
    private ChallengeNNHandler chnnHandler;
    private boolean running = false;
    private int width, height;
    private GraphicsConfiguration graphicsConfiguration;
    private Scene scene;
    private boolean focused = false;
    private String recordedData ="funRec.txt";
    private String frusrecordedData ="frusRec.txt";
    private String chrecordedData ="chRec.txt";
    int frame;
    int delay;
    Thread animator;

    private int ZLevelEnemies = 1;
    private int ZLevelScene = 1;

    public void setGameViewer(GameViewer gameViewer) {
        this.gameViewer = gameViewer;
    }

    private GameViewer gameViewer = null;

    private Agent agent = null;
    private CheaterKeyboardAgent cheatAgent = null;

    private KeyAdapter prevHumanKeyBoardAgent;
    private Mario mario = null;
    private LevelScene levelScene = null;
    private FileWriter fstream;
    BufferedWriter out;
    private FileWriter datastream;
    BufferedWriter data;
	private int g = 0;
    public MarioComponent(int width, int height) {
        adjustFPS();

        this.setFocusable(true);
        this.setEnabled(true);
        this.width = width;
        this.height = height;

        Dimension size = new Dimension(width, height);

        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);

        setFocusable(true);

        if (this.cheatAgent == null)
        {
            this.cheatAgent = new CheaterKeyboardAgent();
            this.addKeyListener(cheatAgent);
        }        

        GlobalOptions.registerMarioComponent(this);
    }

    public void adjustFPS() {
        int fps = GlobalOptions.FPS;
        delay = (fps > 0) ? (fps >= GlobalOptions.InfiniteFPS) ? 0 : (1000 / fps) : 100;
//        System.out.println("Delay: " + delay);
    }

    public void paint(Graphics g) {
    }

    public void update(Graphics g) {
    }

    public void init() {
        graphicsConfiguration = getGraphicsConfiguration();
//        if (graphicsConfiguration != null) {
            Art.init(graphicsConfiguration);
//        }
            
            
			try {
				fstream = new FileWriter("res.txt");
	            datastream = new FileWriter("RecordedData.txt");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 out = new BufferedWriter(fstream);
			 data = new BufferedWriter(datastream);
    }

    public void start() {
        if (!running) {
            running = true;
            animator = new Thread(this, "Game Thread");
            animator.start();
        }
    }

    public void stop() {
        running = false;
    }

    public void run() {

    }

    public EvaluationInfo run1(int currentTrial, int totalNumberOfTrials) {
        running = true;
        adjustFPS();
        EvaluationInfo evaluationInfo = new EvaluationInfo();

        VolatileImage image = null;
        Graphics g = null;
        Graphics og = null;

        image = createVolatileImage(320, 240);
        g = getGraphics();
        og = image.getGraphics();

        if (!GlobalOptions.VisualizationOn) {
            String msgClick = "Vizualization is not available";
            drawString(og, msgClick, 160 - msgClick.length() * 4, 110, 1);
            drawString(og, msgClick, 160 - msgClick.length() * 4, 110, 7);
        }

        addFocusListener(this);

        // Remember the starting time
        long tm = System.currentTimeMillis();
        long tick = tm;
        int marioStatus = Mario.STATUS_RUNNING;

        mario = ((LevelScene) scene).mario;
        int totalActionsPerfomed = 0;
// TODO: Manage better place for this:
        Mario.resetCoins();

        while (/*Thread.currentThread() == animator*/ running) {
            // Display the next frame of animation.
//                repaint();
            scene.tick();
            if (gameViewer != null && gameViewer.getContinuousUpdatesState())
                gameViewer.tick();

            float alpha = 0;

//            og.setColor(Color.RED);
            if (GlobalOptions.VisualizationOn) {
                og.fillRect(0, 0, 320, 240);
                scene.render(og, alpha);
            }

            if (agent instanceof ServerAgent && !((ServerAgent) agent).isAvailable()) {
                System.err.println("Agent became unavailable. Simulation Stopped");
                running = false;
                break;
            }

            boolean[] action = agent.getAction(this/*DummyEnvironment*/);
            if (action != null)
            {
                for (int i = 0; i < Environment.numberOfButtons; ++i)
                    if (action[i])
                    {
                        ++totalActionsPerfomed;
                        break;
                    }
            }
            else
            {
                System.err.println("Null Action received. Skipping simulation...");
                stop();
            }


            //Apply action;
//            scene.keys = action;
            ((LevelScene) scene).mario.keys = action;
            ((LevelScene) scene).mario.cheatKeys = cheatAgent.getAction(null);

            if (GlobalOptions.VisualizationOn) {

                String msg = "Agent: " + agent.getName();
                LevelScene.drawStringDropShadow(og, msg, 0, 7, 5);

                msg = "Selected Actions: ";
                LevelScene.drawStringDropShadow(og, msg, 0, 8, 6);

                msg = "";
                if (action != null)
                {
                    for (int i = 0; i < Environment.numberOfButtons; ++i)
                        msg += (action[i]) ? scene.keysStr[i] : "      ";
                }
                else
                    msg = "NULL";                    
                drawString(og, msg, 6, 78, 1);

                if (!this.hasFocus() && tick / 4 % 2 == 0) {
                    String msgClick = "CLICK TO PLAY";
//                    og.setColor(Color.YELLOW);
//                    og.drawString(msgClick, 320 + 1, 20 + 1);
                    drawString(og, msgClick, 160 - msgClick.length() * 4, 110, 1);
                    drawString(og, msgClick, 160 - msgClick.length() * 4, 110, 7);
                }
                og.setColor(Color.DARK_GRAY);
                LevelScene.drawStringDropShadow(og, "FPS: ", 32, 2, 7);
                LevelScene.drawStringDropShadow(og, ((GlobalOptions.FPS > 99) ? "\\infty" : GlobalOptions.FPS.toString()), 32, 3, 7);

                msg = totalNumberOfTrials == -2 ? "" : currentTrial + "(" + ((totalNumberOfTrials == -1) ? "\\infty" : totalNumberOfTrials) + ")";

                LevelScene.drawStringDropShadow(og, "Trial:", 33, 4, 7);
                LevelScene.drawStringDropShadow(og, msg, 33, 5, 7);

                if (width != 320 || height != 240) {
                        g.drawImage(image, 0, 0, 640 * 2, 480 * 2, null);
                } else {
                    g.drawImage(image, 0, 0, null);
                }
            } else {
                // Win or Die without renderer!! independently.
                marioStatus = ((LevelScene) scene).mario.getStatus();
                if (marioStatus != Mario.STATUS_RUNNING)
                    stop();
            }
            // Delay depending on how far we are behind.
            if (delay > 0)
                try {
                    tm += delay;
                    Thread.sleep(Math.max(0, tm - System.currentTimeMillis()));
                } catch (InterruptedException e) {
                    break;
                }
            // Advance the frame
            frame++;
        }
//=========
        evaluationInfo.agentType = agent.getClass().getSimpleName();
        evaluationInfo.agentName = agent.getName();
        evaluationInfo.marioStatus = mario.getStatus();
        evaluationInfo.livesLeft = mario.lives;
        evaluationInfo.lengthOfLevelPassedPhys = mario.x;
        evaluationInfo.lengthOfLevelPassedCells = mario.mapX;
        evaluationInfo.totalLengthOfLevelCells = levelScene.level.getWidthCells();
        evaluationInfo.totalLengthOfLevelPhys = levelScene.level.getWidthPhys();
        evaluationInfo.timeSpentOnLevel = levelScene.getStartTime();
        evaluationInfo.timeLeft = levelScene.getTimeLeft();
        evaluationInfo.totalTimeGiven = levelScene.getTotalTime();
        evaluationInfo.numberOfGainedCoins = Mario.coins;
//        evaluationInfo.totalNumberOfCoins   = -1 ; // TODO: total Number of coins.
        evaluationInfo.totalActionsPerfomed = totalActionsPerfomed; // Counted during the play/simulation process
        evaluationInfo.totalFramesPerfomed = frame;
        evaluationInfo.marioMode = mario.getMode();
        evaluationInfo.killsTotal = mario.world.killedCreaturesTotal;
//        evaluationInfo.Memo = "Number of attempt: " + Mario.numberOfAttempts;
        if (agent instanceof ServerAgent && mario.keys != null /*this will happen if client quits unexpectedly in case of Server mode*/)
            ((ServerAgent)agent).integrateEvaluationInfo(evaluationInfo);
        return evaluationInfo;
    }

    private void drawString(Graphics g, String text, int x, int y, int c) {
        char[] ch = text.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            g.drawImage(Art.font[ch[i] - 32][c], x + i * 8, y, null);
        }
    }

    public void startLevel(long seed, int difficulty, int type, int levelLength, int timeLimit) {
    	seed = new Random(10).nextLong();
    	scene = new LevelScene(graphicsConfiguration, this, seed, 3, type, levelLength, timeLimit);
        levelScene = ((LevelScene) scene);
        scene.init();
    }

   

    public void focusGained(FocusEvent arg0) {
        focused = true;
    }

    public void focusLost(FocusEvent arg0) {
        focused = false;
    }
    
    public void startCustomLevel(LevelSceneCustom scene){
    	this.scene = scene;
    	this.scene.init();
    }
    /**
     * Part of the fun increaser
     */
    public void toExperienceIncreasedGame(){
    	//set the inputs for the network from the random game first (to get the right type)
    	FunRecorder f = FunRecorder.read(recordedData);
    	nnHandler = new NNHandler(f);
    	FrusRecorder frusrec = FrusRecorder.read(frusrecordedData);
    	frusnnHandler = new FrusNNHandler(frusrec);
    	ChallengeRecorder chrec = ChallengeRecorder.read(chrecordedData);
    	chnnHandler = new ChallengeNNHandler(chrec);
    	try {
    		
			out.write(""+nnHandler.getPerformace()+",");
			out.write(""+frusnnHandler.getPerformace()+",");
			out.write(""+chnnHandler.getPerformace()+",");
			out.newLine();
			data.write("Game: "+g  +"  Agent: "+ agent.getName()+" " );
			data.newLine();
			data.write("FUN: "+f.writeRecorded());
			data.newLine();
			data.write("FRUS: "+frusrec.writeRecorded());
			data.newLine();
			data.write("CHAL: "+chrec.writeRecorded());
			data.write("--------------------------------------");
			data.newLine();
			data.flush();
			out.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		g++;
    	GameType optimizedType = nnHandler.getOptGameType();//getGoalGameType(mlp,1);
    	LevelSceneCustom optimizedLevel = new LevelSceneCustom(graphicsConfiguration,this,optimizedType);
    	
//    	Mario.fire = true;
//    	Mario.large = false;
    	Mario.coins = 0;
    	Mario.lives = 3;
    	
    	startCustomLevel(optimizedLevel);
    }
    public void saveData() {
    	FunRecorder f = new FunRecorder();
    	FunRecorder.fillData(LevelScene.recorder);
    	f.write(recordedData);
    	
    	FrusRecorder fr = new FrusRecorder();
    	FrusRecorder.fillData(LevelScene.recorder);
    	fr.write(frusrecordedData);
    	
    	ChallengeRecorder chrec = new ChallengeRecorder();
    	ChallengeRecorder.fillData(LevelScene.recorder);
    	chrec.write(chrecordedData);
    	
    }
    public void levelFailed() {
//      scene = mapScene;
      Mario.lives--;
      stop();
      saveData();
      try {
		out.write("Level FAIL:  ");
		
		out.flush();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
//      nnHandler = new NNHandler(levelScene.recorder);
//      nnHandler.simulate();
//      toExperienceIncreasedGame();
  }
    
 
    
    public void levelWon() {
        stop();
        saveData();
//        NNHandler nnHandler = new NNHandler(levelScene.recorder);
//        nnHandler.simulate();
//        toExperienceIncreasedGame();

    }

    public void toTitle() {
//        Mario.resetStatic();
//        scene = new TitleScene(this, graphicsConfiguration);
//        scene.init();
    }

    public List<String> getTextObservation(boolean Enemies, boolean LevelMap, boolean Complete, int ZLevelMap, int ZLevelEnemies) {
        if (scene instanceof LevelScene)
            return ((LevelScene) scene).LevelSceneAroundMarioASCII(Enemies, LevelMap, Complete, ZLevelMap, ZLevelEnemies);
        else {
            return new ArrayList<String>();
        }
    }

    public String getBitmapEnemiesObservation()
    {
        if (scene instanceof LevelScene)
            return ((LevelScene) scene).bitmapEnemiesObservation(1);
        else {
            //
            return new String();
        }                
    }

    public String getBitmapLevelObservation()
    {
        if (scene instanceof LevelScene)
            return ((LevelScene) scene).bitmapLevelObservation(1);
        else {
            //
            return null;
        }
    }

    // Chaning ZLevel during the game on-the-fly;
    public byte[][] getMergedObservationZ(int zLevelScene, int zLevelEnemies) {
        if (scene instanceof LevelScene)
            return ((LevelScene) scene).mergedObservation(zLevelScene, zLevelEnemies);
        return null;
    }

    public byte[][] getLevelSceneObservationZ(int zLevelScene) {
        if (scene instanceof LevelScene)
            return ((LevelScene) scene).levelSceneObservation(zLevelScene);
        return null;
    }

    public byte[][] getEnemiesObservationZ(int zLevelEnemies) {
        if (scene instanceof LevelScene)
            return ((LevelScene) scene).enemiesObservation(zLevelEnemies);
        return null;
    }

    public int getKillsTotal() {
        return mario.world.killedCreaturesTotal;
    }

    public int getKillsByFire() {
        return mario.world.killedCreaturesByFireBall;
    }

    public int getKillsByStomp() {
        return mario.world.killedCreaturesByStomp;
    }

    public int getKillsByShell() {
        return mario.world.killedCreaturesByShell;
    }

    public boolean canShoot() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public byte[][] getCompleteObservation() {
        if (scene instanceof LevelScene)
            return ((LevelScene) scene).mergedObservation(this.ZLevelScene, this.ZLevelEnemies);
        return null;
    }

    public byte[][] getEnemiesObservation() {
        if (scene instanceof LevelScene)
            return ((LevelScene) scene).enemiesObservation(this.ZLevelEnemies);
        return null;
    }

    public byte[][] getLevelSceneObservation() {
        if (scene instanceof LevelScene)
            return ((LevelScene) scene).levelSceneObservation(this.ZLevelScene);
        return null;
    }

    public boolean isMarioOnGround() {
        return mario.isOnGround();
    }

    public boolean mayMarioJump() {
        return mario.mayJump();
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
        if (agent instanceof KeyAdapter) {
            if (prevHumanKeyBoardAgent != null)
                this.removeKeyListener(prevHumanKeyBoardAgent);
            this.prevHumanKeyBoardAgent = (KeyAdapter) agent;
            this.addKeyListener(prevHumanKeyBoardAgent);
        }
    }

    public void setMarioInvulnerable(boolean invulnerable)
    {
        Mario.isMarioInvulnerable = invulnerable;
    }

    public void setPaused(boolean paused) {
        levelScene.paused = paused;
    }

    public void setZLevelEnemies(int ZLevelEnemies) {
        this.ZLevelEnemies = ZLevelEnemies;
    }

    public void setZLevelScene(int ZLevelScene) {
        this.ZLevelScene = ZLevelScene;
    }

    public float[] getMarioFloatPos()
    {
        return new float[]{this.mario.x, this.mario.y};
    }

    public float[] getEnemiesFloatPos()
    {
        if (scene instanceof LevelScene)
            return ((LevelScene) scene).enemiesFloatPos();
        return null;
    }

    public int getMarioMode()
    {
        return mario.getMode();
    }

    public boolean isMarioCarrying()
    {
        return mario.carried != null;
    }
}