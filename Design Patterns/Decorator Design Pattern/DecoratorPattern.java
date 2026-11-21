import java.util.*;

// Component Interface: defines a common interface for Mario and all power up decorators.

interface Character{
    String getAbilities();
}

// Concrete Component: Basic Mario Character with no power ups.
class Mario implements Character{
    public String getAbilities(){
        return "Mario";
    }
}


// Abstract Decorator: CharacterDecorator "is-a" Character and "has-a" Character.
abstract class CharacterDecorator implements Character{
    protected Character character; // wrapped components

    public CharacterDecorator(Character c){
        this.character = c;
    }
}

// Concrete Decorator: Height Increasing Power up
class HeightUp extends CharacterDecorator{
    public HeightUp(Character c){
        super(c);
    }

    public String getAbilities(){
        return character.getAbilities() + " with heightUp";
    }
}

// Concrete Decorator: Gun Shooting Power Up
class GunPowerUp extends CharacterDecorator{
    public GunPowerUp(Character c){
        super(c);
    }

    public String getAbilities(){
        return character.getAbilities() + " with Gun";
    }
}

// Concrete Decorator : Star Power Up (temporary Ability).
class StarPowerUp extends CharacterDecorator{
    public StarPowerUp(Character c){
        super(c);
    }

    public String getAbilities(){
        return character.getAbilities() + " with Star Power (limited Time)";
    }
}

public class DecoratorPattern{
    public static void main(String[] args){
        // create a basic Mario Character.
        Character mario = new Mario();
        System.out.println("Basic Character: " + mario.getAbilities());

        //Secorate Mario with a Height Up power Up
        mario = new HeightUp(mario);
        System.out.println("After Height Up: " + mario.getAbilities());

        //Decorate mario further with Gun Power Up.
        mario = new GunPowerUp(mario);
        System.out.println("After GunPowerUp: " + mario.getAbilities());

        // finally, add a StarPowerUp decoration.
        mario = new StarPowerUp(mario);
        System.out.println("After StarPowerUp: " + mario.getAbilities());
    }
}