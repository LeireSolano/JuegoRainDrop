package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class Drop extends ApplicationAdapter {
    private Texture dropImage;
    private Texture bucketImage;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Rectangle bucket;
    private Array<Rectangle> raindrops;
    private long lastDropTime;
    private Sound dropSound;
    private Music rainMusic;
    float tamanio_fuente_puntuacion = 2.0F;
    private BitmapFont contador_puntos;
    private BitmapFont contador_vidas;
    int puntos=0;
    int vidas=3;
    private int altura;


    @Override
    public void create() {
        // load the images for the droplet and the bucket, 64x64 pixels each
        dropImage = new Texture(Gdx.files.internal("drop.png"));
        bucketImage = new Texture(Gdx.files.internal("bucket.png"));

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        batch = new SpriteBatch();

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

        // start the playback of the background music immediately
        rainMusic.setLooping(true);
        rainMusic.play();

        this.altura = Gdx.graphics.getHeight();

        //vamos a crear el contador con estas variables
        contador_puntos = new BitmapFont();
        contador_puntos.setColor(Color.WHITE);
        contador_puntos.getData().setScale(tamanio_fuente_puntuacion);

        contador_vidas = new BitmapFont();
        contador_vidas.setColor(Color.GREEN);
        contador_vidas.getData().setScale(tamanio_fuente_puntuacion);
        // ... more to come ...
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        contador_puntos.draw(batch, "Puntos: "+String.valueOf(puntos), 25, 450);
        contador_vidas.draw(batch, "Vidas: "+String.valueOf(vidas), 675, 450);
        batch.draw(bucketImage, bucket.x, bucket.y);
        for(Rectangle raindrop: raindrops) {
            batch.draw(dropImage, raindrop.x, raindrop.y);

        }
        batch.end();


        if(Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucket.x = touchPos.x - 64 / 2;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.getDeltaTime();

        if(bucket.x < 0) bucket.x = 0;
        //LIMITE DEL CUBO CON LA PANTALLA
        if(bucket.x > 800 - 64) bucket.x = 800 - 64;

        if(TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();

        for (Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext(); ) {
            Rectangle raindrop = iter.next();
            raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
            if(raindrop.y + 64 < 0){
                vidas=vidas-1;
                iter.remove();
            }

            //sonido al coger la gota
            if(raindrop.overlaps(bucket)) {

                if((puntos == 8))
                {
                    vidas = vidas + 1;
                    //nivel = nivel + 1;
                    //velocidad_enemigo = 500.0f;
                    //velocidad_vida = 300.0f;


                }else if((puntos == 18))
                {
                    vidas = vidas + 1;
                    //nivel = nivel + 1;
                    //velocidad_enemigo = 800.0f;
                    //velocidad_vida = 400.0f;
                }else if((puntos == 28))
                {
                    vidas = vidas + 1;
                    //nivel = nivel + 1;
                    //velocidad_enemigo = 1000.0f;
                    //velocidad_vida = 500.0f;
                }else if((puntos == 38))
                {
                    vidas = vidas + 1;
   /*nivel = nivel + 1;
   velocidad_enemigo = 1200.0f;
   velocidad_vida = 600.0f;*/
                }else if((puntos == 48))
                {
                    vidas = vidas + 1;
   /*nivel = nivel + 1;
   velocidad_enemigo = 1400.0f;
   velocidad_vida = 700.0f;*/
                }
                puntos = puntos + 2;
//coger_vida.play();
                //rectangulo_vida.x = 1280;
                //rectangulo_vida.y = MathUtils.random(0,800);
                dropSound.play();
                iter.remove();
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
    public void dispose() {
        dropImage.dispose();
        bucketImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
        contador_puntos.dispose();
        batch.dispose();
    }


}

// rest of class omitted for clarity
