package main.java.Until;

import com.google.gson.GsonBuilder;

public class Message {

    private String name;
    private String text;
    private int index;
    private int size;
    private Command command;
    private Status profile;



    public void setText(String text){
        this.text = text;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setCommand(Command command){
        this.command = command;
    }

    public void setProfile(Status profile){
        this.profile = profile;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setIndex(int index){
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public int getIndex() {
        return index;
    }

    public int getSize() {
        return size;
    }

    public Command getCommand() {
        return command;
    }

    public Status getProfile() {
        return profile;
    }

    public String toJsonString(){
        return new GsonBuilder().create().toJson(this);
    }

    @Override
    public String toString() {
        return "Message{" +
                "name='" + name + '\'' +
                ", text='" + text + '\'' +
                ", index=" + index +
                ", size=" + size +
                ", command=" + command +
                ", profile=" + profile +
                '}';
    }
}
