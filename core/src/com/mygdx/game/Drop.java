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

    //VARIABLES ESTATICAS
    private static final int BUCKET_SIZE = 64;
    private static final int BUCKET_SPEED = 200;
    private Texture dropImage;
    private Texture dropImage2;

    private Texture corazonImage;

    private Texture bucketImage;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Rectangle bucket;
    private Array<Rectangle> raindrops;
    private Array<Rectangle> raindrops2;
    private Array<Rectangle> corazon;
    private long lastDropTime;
    private Sound dropSound;

    private Sound dropSound2;
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
        dropImage2 = new Texture(Gdx.files.internal("drop2.png"));
        bucketImage = new Texture(Gdx.files.internal("bucket.png"));
        corazonImage = new Texture(Gdx.files.internal("corazon.png"));


        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        batch = new SpriteBatch();

        bucket = new Rectangle();
        bucket.x = 800 / 2 - 64 / 2;
        bucket.y = 20;
        bucket.width = BUCKET_SIZE;
        bucket.height = BUCKET_SIZE;

        raindrops = new Array<>();
        raindrops2 = new Array<>();
        corazon = new Array<>();

        spawnRaindrop();



        // load the drop sound effect and the rain background "music"
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        dropSound2 = Gdx.audio.newSound(Gdx.files.internal("drop2.wav"));
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
        ScreenUtils.clear(1, 1, 1, 1);  // R, G, B, A
        //ScreenUtils.clear(0, 0, 0.2f, 1);

        handleInput();
        camera.update();


        updateBucketPosition();
        updateRaindrops(raindrops, dropSound);
        updateRaindrops(raindrops2, dropSound2);


        gotasAzules();
        gotasVerdesMoradas();
        spawnCorazon();
    }

    private void gotasAzules() {
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

                puntos = puntos + 1; //la gota azul suma 1 punto

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
//coger_vida.play();
                //rectangulo_vida.x = 1280;
                //rectangulo_vida.y = MathUtils.random(0,800);
                dropSound.play();
                iter.remove();
            }
        }
    }

    private void gotasVerdesMoradas() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        contador_puntos.draw(batch, "Puntos: "+String.valueOf(puntos), 25, 450);
        contador_vidas.draw(batch, "Vidas: "+String.valueOf(vidas), 675, 450);
        batch.draw(bucketImage, bucket.x, bucket.y);
        for(Rectangle raindrop: raindrops2) {
            batch.draw(dropImage2, raindrop.x, raindrop.y);

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

        for (Iterator<Rectangle> iter = raindrops2.iterator(); iter.hasNext(); ) {
            Rectangle raindrop2 = iter.next();
            raindrop2.y -= 200 * Gdx.graphics.getDeltaTime();

            if(raindrop2.y + 64 < 0){
                vidas=vidas-1;
                iter.remove();
            }

            //sonido al coger la gota
            if(raindrop2.overlaps(bucket)) {

                puntos = puntos + 2; //la gota verde suma 2 puntos

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
//coger_vida.play();
                //rectangulo_vida.x = 1280;
                //rectangulo_vida.y = MathUtils.random(0,800);
                dropSound2.play();
                iter.remove();
            }
        }
    }

    private void spawnCorazon() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        contador_puntos.draw(batch, "Puntos: "+String.valueOf(puntos), 25, 450);
        contador_vidas.draw(batch, "Vidas: "+String.valueOf(vidas), 675, 450);
        batch.draw(bucketImage, bucket.x, bucket.y);
        for(Rectangle raindrop :corazon ) {
            batch.draw(corazonImage, raindrop.x, raindrop.y);

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

        for (Iterator<Rectangle> iter = corazon.iterator(); iter.hasNext(); ) {
            Rectangle cora = iter.next();
            cora.y -= 200 * Gdx.graphics.getDeltaTime();
            if(cora.y + 64 < 0){
                vidas=vidas-1;
                iter.remove();
            }

            vidas = vidas + 1; //el corazon da +1 vida

            iter.remove();
        }
    }

    private void handleInput() {
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucket.x = touchPos.x - BUCKET_SIZE / 2;
        }


        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            bucket.x -= BUCKET_SPEED * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            bucket.x += BUCKET_SPEED * Gdx.graphics.getDeltaTime();
        }


        // Ensure the bucket stays within the screen bounds
        bucket.x = MathUtils.clamp(bucket.x, 0, 800 - BUCKET_SIZE);
    }

    private void updateBucketPosition() {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }
    private void updateRaindrops(Array<Rectangle> raindropsArray, Sound dropSound) {
        Iterator<Rectangle> iter = raindropsArray.iterator();
        while (iter.hasNext()) {
            Rectangle raindrop = iter.next();
            raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
            if (raindrop.y + BUCKET_SIZE < 0) {
                iter.remove();
            }


            if (raindrop.overlaps(bucket)) {
                dropSound.play();
                iter.remove();
                // añadir la puntuacion
            }
        }
    }

    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, 800 - BUCKET_SIZE);
        raindrop.y = 480;
        raindrop.width = BUCKET_SIZE;
        raindrop.height = BUCKET_SIZE;


        if (MathUtils.randomBoolean()) {
            raindrops.add(raindrop);
        } else if (MathUtils.randomBoolean()){
            raindrops2.add(raindrop);
        }else{
            corazon.add(raindrop);
        }


        lastDropTime = TimeUtils.nanoTime();
    }


    @Override
    public void dispose() {
        dropImage.dispose();
        dropImage2.dispose();
        corazonImage.dispose();
        bucketImage.dispose();
        dropSound.dispose();
        dropSound2.dispose();
        rainMusic.dispose();
        batch.dispose();
    }



}

// rest of class omitted for clarity
