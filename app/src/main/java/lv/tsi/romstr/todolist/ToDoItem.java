package lv.tsi.romstr.todolist;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Picture;
import android.media.Image;
import android.widget.Button;
import android.widget.CheckBox;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Roman on 30.10.14..
 */
@Table(name = "Items")
public class ToDoItem extends Model implements Serializable {

    public static final int NO_DATE = -1;
    public static final int COMPLETED = 1;
    public static final int NOT_COMPLETED = 0;
    private static final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
    private static final DateFormat deadlineFormat = new SimpleDateFormat("dd.MM.yy");

    @Column(name = "Text")
    private String text;

    @Column(name = "Date")
    private Date date;

    @Column(name = "Status")
    private boolean completed;

    @Column(name = "Deadline")
    private Date deadline;

    @Column(name = "Description")
    private String description;

    @Column(name = "Picture")
    private String photoPath;

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    private Bitmap photo;


    public ToDoItem() {
        super();
        //this("");
        text = "";
        date = null;
        completed = false;
        deadline = null;
        description = "";
        photoPath = null;
        photo = null;
    }

    public ToDoItem(String text) {
        super();
        this.text = text;
        date = null;
        completed = false;
        deadline = null;
        description = "";
        photoPath = null;
        photo = null;
    }

    public ToDoItem(long id, String text, long date, int completion) {
        super();
        this.text = text;
        if (date != NO_DATE) {
            this.date = new Date(date);
        }
        switch (completion) {
            case COMPLETED:
                completed = true;
                break;
            case NOT_COMPLETED:
                completed = false;
                break;
            default:
                completed = false;
        }
    }

    /*public long getId() {
        return id;
    }*/

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDate(long date) {
        this.date = new Date(date);
    }

    public String getDate() {
        return date == null ? "" : dateFormat.format(date);
    }

    public long getLongDate() {
        return date == null ? NO_DATE : date.getTime();
    }

    public boolean getCompletedStatus() {
        return completed;
    }

    public void unComplete() {
        date = null;
        completed = false;
    }

    public int getCompletion() {
        return completed ? COMPLETED : NOT_COMPLETED;
    }

    public void complete() {
        completed = true;
    }

    public Date getDeadline() {
        return deadline;
    }

    public String deadlineToString() {
        return deadline == null ? "" : deadlineFormat.format(deadline);
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        if (photoPath != null) {
            this.photoPath = photoPath.replace("file:", "");
        } else {
            this.photoPath = null;
        }
    }

    public String getDaysTillDeadline() {
        if (deadline == null) {
            return "\u221e days left";
        } else {
            Calendar calendar = Calendar.getInstance();
            int compareResult = (int) ((deadline.getTime() - calendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
            int compareDayResult = (int) ((deadline.getTime() - calendar.getTime().getTime()) / (10000 * 60 * 60 * 24));
            if (deadline.before(calendar.getTime())) {
                if (compareResult == 0) {
                    return "deadline's today!!!";
                } else {
                    return Math.abs(compareResult) + " day(s) late";
                }
            } else {
                return (compareResult + 1) + " day(s) left";
            }
        }
    }

    public String getStringToShare() {

        String result = "";

        if (completed) {
            result += "I've completed ";
        } else {
            result += "I'm about to complete ";
        }

        result += "the following task:\n\t\"" + text + "\"\n";

        if (!description.equals("")) {
            result += " (" + description + ")\n";
        }

        if (completed) {
            result += "Completed:\n\t on " + getDate() + "\n";
        }

        if (deadline != null) {
            result += "Deadline:\n\t" + deadlineToString() + "\n";
        }

        if (photoPath != null) {
            File photo = new File(photoPath);
            result += "Picture:\n\t\"" + photo.getName() + "\"\n";
        }

        return result;
    }

    public String getTwitterShare() {
        if (completed) {
            return "I've completed \"" + text + "\"!";
        } else {
            return "I'm about to complete \"" + text + "\"!";
        }
    }
}
