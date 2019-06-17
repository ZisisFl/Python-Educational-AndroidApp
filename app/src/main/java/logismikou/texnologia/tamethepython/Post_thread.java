package logismikou.texnologia.tamethepython;

public class Post_thread {

    public String user_id;
    public String thread_title;
    public String thread_keywords;
    public String thread_description;
    public String date;

    public Post_thread(){
        // Default constructor required for calls to DataSnapshot.getValue(CreditCard.class)
    }

    public Post_thread(String user_id, String thread_title, String thread_keywords,
                       String thread_description, String date){

        this.user_id = user_id;
        this.thread_title = thread_title;
        this.thread_keywords = thread_keywords;
        this.thread_description = thread_description;
        this.date = date;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getThread_title() {
        return thread_title;
    }

    public void setThread_title(String thread_title) {
        this.thread_title = thread_title;
    }

    public String getThread_keywords() {
        return thread_keywords;
    }

    public void setThread_keywords(String thread_keywords) {
        this.thread_keywords = thread_keywords;
    }

    public String getThread_description() {
        return thread_description;
    }

    public void setThread_description(String thread_description) {
        this.thread_description = thread_description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
