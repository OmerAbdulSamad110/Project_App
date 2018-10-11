package com.example.omer.project_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DatabaseClass extends SQLiteOpenHelper {
    public static final String Database_Name = "App.db";

    public DatabaseClass(Context context) {
        super(context, Database_Name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Account Table
        db.execSQL("create table account(userId INTEGER PRIMARY KEY AUTOINCREMENT, loggedIn INTEGER, userDP BLOB," +
                "userName TEXT, userFName TEXT, userLName TEXT, userGender TEXT, userEmail TEXT, userPassword TEXT" +
                ", userDOB TEXT, userAddress TEXT, userStudy TEXT, userWork TEXT)");
        //Privacy Table
        db.execSQL("create table privacy(accId INTEGER PRIMARY KEY,postVisible TEXT,postLC TEXT,friendsVisible TEXT" +
                ",dobVisible TEXT, addressVisible TEXT, emailVisible TEXT, sendFR TEXT, sendMsg TEXT, FOREIGN KEY(accId) REFERENCES account(userId))");
        //Friend Request Table
        db.execSQL("create table request(rqId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "sender_Id INTEGER, receiver_Id INTEGER, FOREIGN KEY(sender_Id, receiver_Id) REFERENCES account(userId, userId))");
        //Friend List Table
        db.execSQL("create table friendList(listId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "accId INTEGER, friendId INTEGER, FOREIGN KEY(accId, friendId) REFERENCES account(userId, userId))");
        //Post Table
        db.execSQL("create table post(postId INTEGER PRIMARY KEY AUTOINCREMENT, accId INTEGER , accImg BLOB, postDate TEXT, accName TEXT ,postText TEXT, postImage BLOB, " +
                "postLikes INTEGER, postComments INTEGER, postPrivacy TEXT,hasImage INTEGER ,hasText INTEGER ,FOREIGN KEY(accId, accImg, accName) REFERENCES account(userId, userDP, userName))");
        //Comment Table
        db.execSQL("create table comment(commentId INTEGER PRIMARY KEY AUTOINCREMENT, postId INTEGER, commenterId INTEGER, commenterImg BLOB," +
                "commenterName TEXT ,comment TEXT, commentDate TEXT, commentLikes INTEGER, FOREIGN KEY(postId) REFERENCES post(postId), " +
                "FOREIGN KEY(commenterId, commenterImg, commenterName) REFERENCES account(userId, userDP, userName))");
        //Album Table
        db.execSQL("create table album(albumId INTEGER PRIMARY KEY AUTOINCREMENT, accId, albumName TEXT, albumPrivacy TEXT, thumbnail BLOB" +
                ", FOREIGN KEY(accId) REFERENCES account(userId))");
        //Photo Table
        db.execSQL("create table photo(imageId INTEGER PRIMARY KEY AUTOINCREMENT, albumId INTEGER, accId INTEGER, image BLOB, " +
                "FOREIGN KEY(albumId, accId) REFERENCES album(albumId, accId))");
        //LikeC Table
        db.execSQL("create table likeC (likeId INTEGER PRIMARY KEY AUTOINCREMENT, postId INTEGER, commentId INTEGER, accId INTEGER, " +
                "FOREIGN KEY(postId, commentId) REFERENCES comment(postId, commentId),FOREIGN KEY(accId) REFERENCES account(userId))");
        //LikeP Table
        db.execSQL("create table likeP (likeId INTEGER PRIMARY KEY AUTOINCREMENT, postId INTEGER, accId INTEGER, " +
                "FOREIGN KEY(postId) REFERENCES post(postId), FOREIGN KEY(accId) REFERENCES account(userId))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop table if exists account");
        db.execSQL("Drop table if exists privacy");
        db.execSQL("Drop table if exists request");
        db.execSQL("Drop table if exists friendList");
        db.execSQL("Drop table if exists post");
        db.execSQL("Drop table if exists comment");
        db.execSQL("Drop table if exists album");
        db.execSQL("Drop table if exists photo");
        db.execSQL("Drop table if exists likeC");
        db.execSQL("Drop table if exists likeP");
        onCreate(db);
    }

    //Methods For Account
    public boolean insertAccountData(byte[] userDP, String userName, String userFName, String userLName, String userEmail, String userDOB, String userGender, String userPassword) {


        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("loggedIn", 0);
        contentValues.put("userDP", userDP);
        contentValues.put("userName", userName);
        contentValues.put("userFName", userFName);
        contentValues.put("userLName", userLName);
        contentValues.put("userGender", userGender);
        contentValues.put("userEmail", userEmail);
        contentValues.put("userPassword", userPassword);
        contentValues.put("userDOB", userDOB);
        contentValues.put("userAddress", "");
        contentValues.put("userStudy", "");
        contentValues.put("userWork", "");

        Long result = db.insert("account", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getAllAccountData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from account", null);
        return res;
    }

    public Cursor searchAccounts(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from account except select * from account where userId = " + userId, null);
        return res;
    }

    public Cursor getSpecificAccountData(String name, String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from account where userName = '" + name + "' and userEmail = '" + email + "'", null);
        return res;
    }

    public Cursor getSpecificAccountData(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from account where userId = " + userId, null);
        return res;
    }

    public void updateAccountData(int userId, String colName, String colValue) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("update account set " + colName + " = '" + colValue + "' where userId = " + userId);
    }

    public void updateAccountDP(int userId, byte[] userDP) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("userDP", userDP);
        db.update("account", cv, "userId is " + userId, null);
    }

    public void log_inOrout(String email, int id, int i) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("UPDATE account SET loggedIn =" + i + " WHERE userId = " + id + " and userEmail = '" + email + "'");
    }

    public void deleteAccount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM account where userId = " + userId);
    }

    //Methods For Privacy
    public boolean insertPrivacyData(int accId) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("accId", accId);
        //3-op
        contentValues.put("postVisible", "Public");
        contentValues.put("postLC", "Public");
        contentValues.put("friendsVisible", "Public");
        contentValues.put("dobVisible", "Public");
        contentValues.put("addressVisible", "Public");
        contentValues.put("emailVisible", "Public");
        //2-op
        contentValues.put("sendFR", "Every one");
        contentValues.put("sendMsg", "Evey one");

        Long result = db.insert("privacy", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getSpecificPrivacy(int accId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from privacy where accId = " + accId, null);
        return res;
    }

    public void updatePrivacy(int accId, String colName, String colValue) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("UPDATE privacy SET " + colName + " = '" + colValue + "' WHERE accId = " + accId);
    }

    //Methods for Album
    public boolean insertAlbumData(int accId, String albumName, String albumPrivacy, byte[] thumbnail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("accId", accId);
        contentValues.put("albumName", albumName);
        contentValues.put("albumPrivacy", albumPrivacy);
        contentValues.put("thumbnail", thumbnail);

        Long result = db.insert("album", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public void updateAlbumTN(int albumId, int accId, byte[] thumbnail) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("thumbnail", thumbnail);
        db.update("album", cv, "accId is " + accId + " and albumId is " + albumId, null);
    }

    public void updatePostTN(int accId, byte[] thumbnail) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("accImg", thumbnail);
        db.update("post", cv, "accId is " + accId, null);
    }

    public void updateCommentTN(int accId, byte[] thumbnail) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("commenterImg", thumbnail);
        db.update("comment", cv, "commenterId is " + accId, null);
    }


    public Cursor getAllAlbum(int accId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from album where accId = " + accId, null);
        return res;
    }

    public Cursor getSpecificAlbum(int albumId, int accId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from album where accId = " + accId + " and albumId = " + albumId, null);
        return res;
    }

    public Cursor getSpecificAlbumId(int accId, String albumName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select album.albumId from album where accId = " + accId + " and albumName = '" + albumName + "'", null);
        return res;
    }

    public void updateAlbumPrivacy(int albumId, int accId, String privacy) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("update album set albumPrivacy = '" + privacy + "' WHERE accId = " + accId + " and albumId = " + albumId);
    }

    public void deleteAlbum(int albumId, int accId) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from album where accId = " + accId + " and albumId = " + albumId);
    }

    //Methods for Photos
    public boolean insertPhotoData(int accId, int albumId, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("accId", accId);
        contentValues.put("albumId", albumId);
        contentValues.put("image", image);

        Long result = db.insert("photo", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public void deletePhoto(int albumId, int accId, int imageId) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from photo where accId = " + accId + " and albumId = " + albumId + " and imageId = " + imageId);
    }

    public void deleteAllPhotos(int albumId, int accId) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from photo where accId = " + accId + " and albumId = " + albumId);
    }


    public Cursor getSpecificPhoto(int albumId, int accId, int imageId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from photo where accId = " + accId + " and albumId = " + albumId + " and imageId = " + imageId, null);
        return res;
    }

    public Cursor getAllPhoto(int albumId, int accId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from photo where accId = " + accId + " and albumId = " + albumId, null);
        return res;
    }

    //Methods for Friend Request
    public boolean sendRequest(int sender_Id, int receiver_Id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("sender_Id", sender_Id);
        contentValues.put("receiver_Id", receiver_Id);

        Long result = db.insert("request", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor reqData(int receiver_Id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select request.rqId, account.userId, account.userDP, account.userName, account.userEmail from request inner join account" +
                " on request.sender_Id = account.userId where request.receiver_Id = " + receiver_Id, null);
        return res;
    }

    public Cursor reqSend(int sender_Id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select request.receiver_Id from request where sender_Id = " + sender_Id, null);
        return res;
    }

    public Cursor reqGot(int receiver_Id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select request.sender_Id from request where request.receiver_Id = " + receiver_Id, null);
        return res;
    }

    public void reqCancel(int sender_Id, int receiver_Id) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from request where sender_Id = " + sender_Id + " and receiver_Id = " + receiver_Id);
    }

    //Methods for Friend list
    public boolean insertFriendList(int accId, int friendId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("accId", accId);
        contentValues.put("friendId", friendId);

        Long result = db.insert("friendList", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor friendList(int friendId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select friendList.listId, account.userId, account.userDP, account.userName, account.userEmail from friendList inner join account" +
                " on friendList.accId = account.userId where friendList.friendId = " + friendId, null);
        return res;
    }

    public Cursor allFriendList(int accId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from friendList where friendList.friendId = " + accId, null);
        return res;
    }

    public void removeFriend(int accId, int friendId) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from friendList where accId = " + accId + " and friendId = " + friendId);
    }

    //Methods for Post

    public boolean insertPostData(int accId, byte[] accImg, String accName, String postDate, String postText, byte[] postImage, String postPrivacy, int hasImage, int hasText) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("accId", accId);
        contentValues.put("accImg", accImg);
        contentValues.put("accName", accName);
        contentValues.put("postDate", postDate);
        contentValues.put("postText", postText);
        contentValues.put("postImage", postImage);
        contentValues.put("postLikes", 0);
        contentValues.put("postComments", 0);
        contentValues.put("postPrivacy", postPrivacy);
        contentValues.put("hasImage", hasImage);
        contentValues.put("hasText", hasText);

        Long result = db.insert("post", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor recycleViewPost(int accId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from post where accId = " + accId, null);
        return res;
    }

    public Cursor postOpened(int postId, int accId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from post where postId = " + postId + " and accId = " + accId, null);
        return res;
    }

    public void editPosts(int postId, int accId, String postText) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("update post set postText = '" + postText + "' WHERE postId = " + postId + " and accId = " + accId);
    }

    public void updatePostPrivacy(int postId, int accId, String postPrivacy) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("update post set postPrivacy = '" + postPrivacy + "' WHERE postId = " + postId + " and accId = " + accId);
    }

    public void insertPostLikes(int postId, int accId, int postLikes) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("update post set postLikes = " + postLikes + " WHERE postId = " + postId + " and accId = " + accId);
    }

    public void deletePost(int postId, int accId) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from post where accId = " + accId + " and postId = " + postId);
    }

    //like Post Methods
    public boolean insertLikeP(int postId, int accId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("postId", postId);
        contentValues.put("accId", accId);

        Long result = db.insert("likeP", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor viewLikeP(int postId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from likeP where postId = " + postId, null);
        return res;
    }

    public Cursor viewActiveLikeP(int postId, int accId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from likeP where postId = " + postId + " and accId = " + accId, null);
        return res;
    }

    public void deleteAllLikeP(int postId) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from likeP where postId = " + postId);
    }

    public void deleteLikeP(int postId, int accId) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from likeP where postId = " + postId + " and accId = " + accId);
    }

    //Methods for comment

    public boolean insertCommentData(int postId, int commenterId, byte[] commenterImg, String commenterName, String comment, String commentDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("postId", postId);
        contentValues.put("commenterId", commenterId);
        contentValues.put("commenterImg", commenterImg);
        contentValues.put("commenterName", commenterName);
        contentValues.put("comment", comment);
        contentValues.put("commentDate", commentDate);
        contentValues.put("commentLikes", 0);

        Long result = db.insert("comment", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor viewComment(int postId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from comment where postId = " + postId, null);
        return res;
    }

    public Cursor viewSpecificC(int postId, int commentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from comment where postId = " + postId + " and commentId = " + commentId, null);
        return res;
    }

    public void editComments(int postId, int commentId, String comment) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("update comment set comment = '" + comment + "' WHERE postId = " + postId + " and commentId = " + commentId);
    }


    public void insertCommentLikes(int postId, int commentId, int commentLikes) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("update comment set commentLikes = '" + commentLikes + "' WHERE postId = " + postId + " and commentId = " + commentId);
    }

    public void deleteComment(int postId, int commentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from comment where postId = " + postId + " and commentId = " + commentId);
    }

    public void deleteAllComments(int postId) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from comment where postId = " + postId);
    }

    //like Comment Methods
    public boolean insertLikeC(int postId, int commentId, int accId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("postId", postId);
        contentValues.put("commentId", commentId);
        contentValues.put("accId", accId);

        Long result = db.insert("likeC", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor viewLikeC(int postId, int commentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from likeC where postId = " + postId + " and commentId = " + commentId, null);
        return res;
    }

    public Cursor viewActiveLikeC(int postId, int commentId, int activeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from likeC where postId = " + postId + " and commentId = " + commentId + " and accId = " + activeId, null);
        return res;
    }

    public void deleteAllLikeC(int postId) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from likeC where postId = " + postId);
    }

    public void deleteLikeC(int postId, int accId, int commentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from likeC where postId = " + postId + " and accId = " + accId + " and commentId = " + commentId);
    }

    //Methods for Newsfeed
    public Cursor newsFeed(int activeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select post.postId, account.userId, account.userDP, account.userName, post.PostText,post.postImage, " +
                "post.postDate, post.postPrivacy, privacy.postLC, post.hasImage, post.hasText from friendList inner join account on friendList.accId = account.userId inner join " +
                "post on post.accId = account.userId inner join privacy on privacy.accId = account.userId where friendList.friendId = " + activeId + " or account.userId = " + activeId, null);
        return res;
    }

    public Cursor newsFeed1(int activeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select post.postId, userId, account.userDP, account.userName, post.PostText,post.postImage, "+
                "post.postDate, post.postPrivacy, privacy.postLC, post.hasImage, post.hasText from post inner join "+
                "account on post.accId = account.userId inner join privacy on privacy.accId = account.userId where account.userId = "+activeId, null);
        return res;
    }


    //Delete methods

    private void deleteall1(int accId) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from likeC where accId = " + accId);
    }
    private void deleteall2(int accId) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from likeP where accId = " + accId);
    }

    private void deleteall3(int accId) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from friendList where accId = " + accId);
    }
    private void deleteall4(int accId) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from request where receiver_Id = " + accId);
    }

    private void deleteall5(int accId) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from request where sender_Id = " + accId);
    }

    private void deleteall6(int accId) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from account where userId = " + accId);
    }

    private void deleteall7(int accId) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from post where accId = " + accId);
    }

    private void deleteall8(int accId) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from comment where commenterId = " + accId);
    }

    private void deleteall9(int accId) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from album where accId = " + accId);
    }

    private void deleteall10(int accId) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from album where accId = " + accId);
    }

    private void deleteall11(int accId) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from privacy where accId = " + accId);
    }

    public void deleteAll(int accId)
    {
        deleteall1(accId);
        deleteall2(accId);
        deleteall3(accId);
        deleteall4(accId);
        deleteall5(accId);
        deleteall6(accId);
        deleteall7(accId);
        deleteall8(accId);
        deleteall9(accId);
        deleteall10(accId);
        deleteall11(accId);
    }

}