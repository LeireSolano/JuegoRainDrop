package com.mygdx.game;

import java.util.Iterator;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
    private Texture dropImage2;
    private Texture corazonImage;
    private Texture bucketImage;
    private OrthographicCamera camera;
    private Rectangle bucket;
    private Array<Rectangle> raindrops;
    private Array<Rectangle> raindrops2;
    private Array<Rectangle> corazones;
    private Sound dropSound;
    private Sound dropSound2;
    private Music rainMusic;

    // controlar el tiempo de spawn de los corazones
    private long lastCorazonTime;
    private long corazonSpawnInterval = 3500000000L; // Cambia este valor según la frecuencia deseada (en nanosegundos)

    // Controlar el tiempo de spawn de las gotas1
    private long lastDropTime;
    private long dropSpawnInterval = 900000000L; // Cambia este valor según la frecuencia deseada (en nanosegundos)

    // Controlar el tiempo de spawn de las gotas2
    private long lastDropTime2;
    private long dropSpawnInterval2 = 700000000L; // Cambia este valor según la frecuencia deseada (en nanosegundos)

    int puntos=0;
    int vidas=3;
    private int altura;
    private Texture fondo;


    public GameScreen(final Drop gam) {
        this.game = gam;

        // load the images for the droplet and the bucket, 64x64 pixels each
        dropImage = new Texture(Gdx.files.internal("drop.png"));
        dropImage2 = new Texture(Gdx.files.internal("drop2.png"));
        corazonImage = new Texture(Gdx.files.internal("corazon.png"));
        bucketImage = new Texture(Gdx.files.internal("bucket.png"));

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        bucket = new Rectangle();
        bucket.x = 800 / 2 - 64 / 2;
        bucket.y = 20;
        bucket.width = 64;
        bucket.height = 64;

        raindrops = new Array<Rectangle>();
        //creo los arrays para las nuevs gotas
        raindrops2 = new Array<Rectangle>();
        corazones = new Array<Rectangle>();

        spawnRaindrop(); //creamos la gota
        spawnRaindrop2(); //creamos la gota2
        spawnCorazon(); //creamos el corazon


        // load the drop sound effect and the rain background "music"
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        dropSound2 = Gdx.audio.newSound(Gdx.files.internal("drop2.wav"));
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

        //bucle para las gotas 2
        for(Rectangle raindrop: raindrops2) {
            game.batch.draw(dropImage2, raindrop.x, raindrop.y);
        }

        // bucle para el corazon
        for(Rectangle raindrop: corazones) {
            game.batch.draw(corazonImage, raindrop.x, raindrop.y);
        }

        game.batch.end();

        /*
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
        */

        //boolean gyroscopeAvail = Gdx.input.isPeripheralAvailable(Input.Peripheral.Gyroscope);
        boolean available = Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer);

        int orientation = Gdx.input.getRotation();
        Input.Orientation nativeOrientation = Gdx.input.getNativeOrientation();

        if (available) {
            float accelX = Gdx.input.getAccelerometerX();
            float accelY = Gdx.input.getAccelerometerY();
            //float accelZ = Gdx.input.getAccelerometerZ();

            float movementSpeed = 300f; // Puedes ajustar este valor según la velocidad que desees
            float deltaX = +accelY * Gdx.graphics.getDeltaTime() * movementSpeed;

            bucket.x += deltaX;

        }

        /*
        if (gyroscopeAvail) {
            float gyroX = Gdx.input.getGyroscopeX();
            // Ajusta la velocidad del movimiento según el valor del giroscopio
            float movementSpeed = 500f; // Puedes ajustar este valor según la velocidad que desees

            // Calcula el cambio de posición basado en el valor del giroscopio
            float deltaX = +gyroX * Gdx.graphics.getDeltaTime() * movementSpeed;

            // Actualiza la posición del cubo (bucket)
            bucket.x += deltaX;
        }
        */

        if(bucket.x < 0) bucket.x = 0;
        //LIMITE DEL CUBO CON LA PANTALLA
        if(bucket.x > 800 - 64) bucket.x = 800 - 64;

        // comprobamos si necesitamos crear mas gotas
        if(TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();
        if(TimeUtils.nanoTime() - lastDropTime2 > 1000000000) spawnRaindrop2();
        if (TimeUtils.nanoTime() - lastCorazonTime > 900000000) spawnCorazon();




        //GOTAS 1
        // quitamos las gotas que estan mas alla del limite de la pantalla o que han chocado con el cubo
        Iterator<Rectangle> iter = raindrops.iterator();
        while (iter.hasNext()) {
            Rectangle raindrop = iter.next();
            raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
            if(raindrop.y + 64 < 0){
                //vidas=vidas-1;
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
        }//fin while

        // GOTAS 2
        Iterator<Rectangle> iter2 = raindrops2.iterator();
        while (iter2.hasNext()) {
            Rectangle raindrop2 = iter2.next();
            raindrop2.y -= 200 * Gdx.graphics.getDeltaTime();
            if(raindrop2.y + 64 < 0){
                //puntos = puntos-1;
                iter2.remove();
            }

            //sonido al coger la gota
            if(raindrop2.overlaps(bucket)) {
                vidas = vidas -1;
                dropSound2.play();

                iter2.remove();
            }// fin del if del buckect

            if (vidas <=0){

                dispose();
                game.setScreen(new GameOverScreen( game));

            }
        }//fin while

        //CORAZONES
        Iterator<Rectangle> iter3 = corazones.iterator();
        while (iter3.hasNext()) {
            Rectangle corazon = iter3.next(); // Cambiado 'raindrop' por 'corazon'
            corazon.y -= 200 * Gdx.graphics.getDeltaTime(); // Cambiado 'raindrop' por 'corazon'
            if(corazon.y + 64 < 0){ // Cambiado 'raindrop' por 'corazon'
                //vidas=vidas-1;
                iter3.remove();
            }

            //sonido al coger el corazón
            if(corazon.overlaps(bucket)) { // Cambiado 'raindrop' por 'corazon'
                vidas++;
                dropSound.play();

                iter3.remove();
            }// fin del if del corazón

            if (vidas <=0){
                dispose();
                game.setScreen(new GameOverScreen( game));
            }
        }//fin while


    }//fin render

    private void spawnRaindrop2() {
        if (TimeUtils.nanoTime() - lastDropTime2 > dropSpawnInterval2) {
            Rectangle raindrop2 = new Rectangle();
            raindrop2.x = MathUtils.random(0, 800-64);
            raindrop2.y = 480;
            raindrop2.width = 64;
            raindrop2.height = 64;
            raindrops2.add(raindrop2);
            lastDropTime2 = TimeUtils.nanoTime();
        }
    }

    private void spawnRaindrop() {
        // Crear gota1 solo si ha pasado suficiente tiempo desde la última generación
        if (TimeUtils.nanoTime() - lastDropTime > dropSpawnInterval) {
            // Crear gota
            Rectangle raindrop = new Rectangle();
            raindrop.x = MathUtils.random(0, 800-64);
            raindrop.y = 480;
            raindrop.width = 64;
            raindrop.height = 64;
            raindrops.add(raindrop);

            // Actualizar el tiempo de la última generación de gota
            lastDropTime = TimeUtils.nanoTime();
        }
    }

    private void spawnCorazon() {
        // Crear corazón solo si ha pasado suficiente tiempo desde la última generación
        if (TimeUtils.nanoTime() - lastCorazonTime > corazonSpawnInterval) {
            // Crear corazón
            Rectangle corazon = new Rectangle();
            corazon.x = MathUtils.random(0, 800-64);
            corazon.y = 480;
            corazon.width = 64;
            corazon.height = 64;
            corazones.add(corazon);

            // Actualizar el tiempo de la última generación de corazón
            lastCorazonTime = TimeUtils.nanoTime();
        }
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
        dropImage2.dispose();
        corazonImage.dispose();
        bucketImage.dispose();
        dropSound.dispose();
        dropSound2.dispose();
        rainMusic.dispose();
        //game.batch.dispose();
    }


}