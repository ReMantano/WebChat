package Until;


import java.util.Objects;

import javax.websocket.Session;

public class Profile {

    private String name;
    private final Session selfSession;
    private Session connection;
    private StringBuffer sendMessageInVoid = new StringBuffer();
    private Status status;

    public Profile(Session selfSession){
        this.selfSession = selfSession;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public Session getSelfWriter() {
        return selfSession;
    }

    public Session getConnection() {
        return connection;
    }

    public void setConnection(Session connection) {
        this.connection = connection;
    }

    public void setStatus(Status status){
        this.status = status;
    }

    public void setName(String name){
        this.name = name;
    }

    public void addMessageInVoid(String message){
        sendMessageInVoid.append(
                name+ ": "+
                message+ "\n");
    }

    public String getMessageInVoid(){
        return sendMessageInVoid.toString();
    }

    public void clearMessageInVoid(){
        sendMessageInVoid.setLength(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return Objects.equals(name, profile.name) &&
                Objects.equals(status, profile.status) &&
                Objects.equals(selfSession, profile.selfSession) &&
                Objects.equals(connection, profile.connection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, status, selfSession, connection);
    }

    @Override
    public String toString() {
        return "Profile{" +
                "name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", selfWriter=" + selfSession +
                ", connection=" + connection +
                '}';
    }
}
