package com.mygdx.game;

import java.util.Iterator;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;

public class GameScreen implements Screen {

    final Drop game;

    private Texture dropImage;
    private Texture bucketImage;
    private OrthographicCamera camera;
    private Rectangle bucket;
    private Array<Rectangle> raindrops;
    private Array<Rectangle> corazones;
    private long lastDropTime;
    private Sound dropSound;
    private Music rainMusic;

    int puntos=0;
    int vidas=3;
    private int altura;
    private Texture fondo;


    public GameScreen(final Drop gam) {
        this.game = gam;

        // load the images for the droplet and the bucket, 64x64 pixels each
        dropImage = new Texture(Gdx.files.internal("drop.png"));
        bucketImage = new Texture(Gdx.files.internal("bucket.png"));

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        bucket = new Rectangle();
        bucket.x = 800 / 2 - 64 / 2;
        bucket.y = 20;
        bucket.width = 64;
        bucket.height = 64;

        raindrops = new Array<Rectangle>();
        spawnRaindrop();




        // load the drop sound effect and the rain background "music"
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        rainMusic.setLooping(true); // start the playback of the background music immediately

        //this.altura = Gdx.graphics.getHeight();

        // fondo del juego

        fondo = new Texture(Gdx.files.internal("lluviatriste.jpg"));

    }

    @Override
    public void render(float delta) {
        //ScreenUtils.clear(0, 0, 0.2f, 1);// no hace falta pq esto es para el color del fondo y el nuestro es una imagen

        camera.update();

        game.batch.setProjectionMatrix(camera.combined);


        game.batch.begin();

        game.batch.draw(fondo, 0, 0, 800,500);
        game.font_cont_pts.draw(game.batch, "Puntos: "+puntos, 25, 450);
        game.font_cont_vidas.draw(game.batch, "Vidas: "+ vidas, 675, 450);
        game.batch.draw(bucketImage, bucket.x, bucket.y);

        for(Rectangle raindrop: raindrops) {
            game.batch.draw(dropImage, raindrop.x, raindrop.y);
        }

        game.batch.end();


        if(Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucket.x = touchPos.x - 64 / 2;
        }

        if (Gdx.input.isKeyPressed(Keys.LEFT))
            bucket.x -= 200 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Keys.RIGHT))
            bucket.x += 200 * Gdx.graphics.getDeltaTime();


        if(bucket.x < 0) bucket.x = 0;
        //LIMITE DEL CUBO CON LA PANTALLA
        if(bucket.x > 800 - 64) bucket.x = 800 - 64;

        // comprobamos si necesitamos crear mas gotas
        if(TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();

        // quitamos las gotas que estan mas alla del limite de la pantalla o que han chocado con el cubo
        Iterator<Rectangle> iter = raindrops.iterator();
        while (iter.hasNext()) {
            Rectangle raindrop = iter.next();
            raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
            if(raindrop.y + 64 < 0){
                vidas=vidas-1;
                iter.remove();
            }

            //sonido al coger la gota
            if(raindrop.overlaps(bucket)) {
                puntos = puntos + 1;
                dropSound.play();

                iter.remove();
            }// fin del if del buckect

            if (vidas <=0){

                dispose();
                game.setScreen(new GameOverScreen( game));

            }
        }
    }

    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, 800-64);
        raindrop.y = 480;
        raindrop.width = 64;
        raindrop.height = 64;
        raindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }


    @Override
    public void resize(int width, int height) {
    }


    @Override
    public void show() {
        // start the playback of the background music when the screen is shown
        rainMusic.play();
    }
    @Override
    public void hide() {
    }


    @Override
    public void pause() {
    }


    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        dropImage.dispose();
        bucketImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
        //game.batch.dispose();
    }


}