import java.util.*;

interface ISubscriber{
    void update();
}

// Observable Interface : a youtube channel interface
interface IChannel{
    void subscribe(ISubscriber subscriber);
    void unsubscribe(ISubscriber subscriber);
    void notifySubscribers();
}

// Concrete Subject : a youtube channel that observes can sunscribe to
class Channel implements IChannel{
    private List<ISubscriber> subscribers;
    private String name;
    private String latestVideo;

    public Channel(String name){
        this.name = name;
        this.subscribers = new ArrayList<>();
    }

    @Override 
    public void subscribe(ISubscriber subscriber){
        if(!subscribers.contains(subscriber)){
            subscribers.add(subscriber);
        }
    }

    @Override 
    public void unsubscribe(ISubscriber subscriber){
        subscribers.remove(subscriber);
    }

    @Override 
    public void notifySubscribers(){
        for(ISubscriber sub : subscribers){
            sub.update();
        }
    }

    public void uploadVideo(String title){
        latestVideo = title;
        System.out.println("\n["+ name + " uploaded \"" + title + "\"]");
        notifySubscribers();
    }

    public String getVideoData(){
        return "\nCheckout our new Video : " + latestVideo + "\n";
    }


}

// Concrete Observer: represents a subscriber to the channel

class Subscriber implements ISubscriber{
    private String name;
    private Channel channel;

    public Subscriber(String name, Channel channel){
        this.name = name;
        this.channel = channel;
    }

    @Override 
    public void update(){
        System.out.println("Hey " + name + "," + channel.getVideoData());
    }
}

public class ObserverDesignPattern{
    public static void main(String[] args){
        //create  channel and subscribers
        Channel channel = new Channel("CoderArmy");

        Subscriber subs1 = new Subscriber("Varun", channel);
        Subscriber subs2 = new Subscriber("Tarun", channel);

        //Varun and Tarun subscribe to coderArmy
        channel.subscribe(subs1);
        channel.subscribe(subs2);

        // upload a video: both varun and tarun are notified
        channel.uploadVideo("Observer Pattern Tutorial");

        // Varun unsuscribes ; tarun remains subscribed
        channel.unsubscribe(subs1);

        // upload another video : only tarun is notified
        channel.uploadVideo("Decorator Pattern Tutorial");
    }
}