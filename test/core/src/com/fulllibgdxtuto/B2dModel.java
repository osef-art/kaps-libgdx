package com.fulllibgdxtuto;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static com.badlogic.gdx.physics.box2d.BodyDef.*;

public class B2dModel {
    public World world;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;
    private Body dynamicBody, staticBody, kinematicBody;

    public B2dModel(){
        world = new World(new Vector2(0,-10f), true);
        createFloor();
        createObject();
        createMovingObject();

        // get our body factory singleton and store it in bodyFactory
        BodyFactory bodyFactory = BodyFactory.getInstance(world);

        // add a new rubber ball at position 1, 1
        bodyFactory.makeCirclePolyBody(3, 1, 2, BodyFactory.RUBBER, BodyType.DynamicBody,false);

        // add a new steel ball at position 4, 1
        bodyFactory.makeCirclePolyBody(4, 1, 2, BodyFactory.STEEL, BodyType.DynamicBody,false);

        // add a new stone at position -4,1
        bodyFactory.makeCirclePolyBody(-4, 1, 2, BodyFactory.STONE, BodyType.DynamicBody,false);
    }

    // our game logic here
    public void logicStep(float delta){
        world.step(delta, 3, 3);
    }

    private void createObject(){
        //create a new body definition (type and location)
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.position.set(0,0);


        // add it to the world
        dynamicBody = world.createBody(bodyDef);

        // set the shape (here we use a box 50 meters wide, 1 meter tall )
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1,1);

        // set the properties of the object ( shape, weight, restitution(bounciness)
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        // create the physical object in our body)
        // without this our body would just be data in the world
        dynamicBody.createFixture(shape, 0.0f);

        // we no longer use the shape object here so dispose of it.
        shape.dispose();
    }

    private void createFloor() {
        // create a new body definition (type and location)
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;
        bodyDef.position.set(0, -10);
        // add it to the world
        staticBody = world.createBody(bodyDef);
        // set the shape (here we use a box 50 meters wide, 1 meter tall )
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(50, 1);
        // create the physical object in our body)
        // without this our body would just be data in the world
        staticBody.createFixture(shape, 0.0f);
        // we no longer use the shape object here so dispose of it.
        shape.dispose();
    }

    private void createMovingObject(){

        //create a new body definition (type and location)
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.KinematicBody;
        bodyDef.position.set(0,-12);


        // add it to the world
        kinematicBody = world.createBody(bodyDef);

        // set the shape (here we use a box 50 meters wide, 1 meter tall )
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1,1);

        // set the properties of the object ( shape, weight, restitution(bounciness)
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        // create the physical object in our body)
        // without this our body would just be data in the world
        kinematicBody.createFixture(shape, 0.0f);

        // we no longer use the shape object here so dispose of it.
        shape.dispose();

        kinematicBody.setLinearVelocity(0, 0.75f);
    }
}