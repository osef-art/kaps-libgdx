package com.fulllibgdxtuto;

import com.badlogic.gdx.physics.box2d.*;

import static com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import static com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody;

public class BodyFactory {
    private static BodyFactory thisInstance;
    public static final int RUBBER = 2;
    public static final int STEEL = 0;
    public static final int WOOD = 1;
    public static final int STONE = 3;
    private final World world;

    private BodyFactory(World world){
        this.world = world;
        thisInstance = this;
    }

    public static BodyFactory getInstance(World world){
        if(thisInstance == null) {
            thisInstance = new BodyFactory(world);
        }
        return thisInstance;
    }

    static public FixtureDef makeFixture(int material, Shape shape){
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;

        switch (material) {
            case 0:
                fixtureDef.density = 1f;
                fixtureDef.friction = 0.3f;
                fixtureDef.restitution = 0.1f;
                break;
            case 1:
                fixtureDef.density = 0.5f;
                fixtureDef.friction = 0.7f;
                fixtureDef.restitution = 0.3f;
                break;
            case 2:
                fixtureDef.density = 1f;
                fixtureDef.friction = 0f;
                fixtureDef.restitution = 1f;
                break;
            case 3:
                fixtureDef.density = 1f;
                fixtureDef.friction = 0.9f;
                fixtureDef.restitution = 0.01f;
            default:
                fixtureDef.density = 7f;
                fixtureDef.friction = 0.5f;
                fixtureDef.restitution = 0.3f;
        }
        return fixtureDef;
    }

    public Body makeCirclePolyBody(float posX, float posY, float radius,
                                   int material, BodyType bodyType,
                                   boolean fixedRotation) {
        // create a definition
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = bodyType;
        boxBodyDef.position.x = posX;
        boxBodyDef.position.y = posY;
        boxBodyDef.fixedRotation = fixedRotation;

        //create the body to attach said definition
        Body boxBody = world.createBody(boxBodyDef);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius /2);
        boxBody.createFixture(makeFixture(material,circleShape));
        circleShape.dispose();
        return boxBody;
    }

    public Body makeCirclePolyBody(float posX, float posY, float radius, int material, BodyType bodyType){
        return makeCirclePolyBody(posX, posY, radius, material, bodyType, false);
    }

    public Body makeCirclePolyBody(float posX, float posY, float radius, int material){
        return makeCirclePolyBody( posX,  posY,  radius,  material, DynamicBody, false);
    }

    public Body makeBoxPolyBody(float posX, float posY, float width, float height,int material, BodyType bodyType){
        return makeBoxPolyBody(posX, posY, width, height, material, bodyType, false);
    }

    public Body makeBoxPolyBody(float posX, float posY, float width, float height,int material, BodyType bodyType, boolean fixedRotation){
        // create a definition
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = bodyType;
        boxBodyDef.position.x = posX;
        boxBodyDef.position.y = posY;
        boxBodyDef.fixedRotation = fixedRotation;

        //create the body to attach said definition
        Body boxBody = world.createBody(boxBodyDef);
        PolygonShape poly = new PolygonShape();
        poly.setAsBox(width/2, height/2);
        boxBody.createFixture(makeFixture(material,poly));
        poly.dispose();

        return boxBody;
    }
}