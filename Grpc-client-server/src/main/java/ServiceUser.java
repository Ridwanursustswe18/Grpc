import com.demo.grpc.User;
import com.demo.grpc.userGrpc;
import io.grpc.stub.StreamObserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import static java.sql.DriverManager.getConnection;

public class ServiceUser extends userGrpc.userImplBase {
    String databaseURL = "jdbc:mysql://localhost:3306/clien_info";
    String user = "root";
    String password = "";
    private static final Logger logger = Logger.getLogger(ServiceUser.class.getName());
@Override
    public void login(User.LoginRequest request, StreamObserver<User.LoginResponse> responseObserver) throws SQLException, ClassNotFoundException {
    String userName = request.getUsername();
    String password = request.getPassword();
    ResultSet resultSet = checkLogInCredential(userName,password);
    User.LoginResponse.Builder response = User.LoginResponse.newBuilder();
logger.info("login attempted from "+userName);
    while (resultSet.next()){
        if(resultSet.getInt(1) == 1){
            response.setResCode(200).setMessage("Successfull");
        }
        else {
            response.setResCode(401).setMessage("Unathorized");
        }
        break;
    }
    responseObserver.onNext(response.build());
    responseObserver.onCompleted();
}
@Override
    public void userRegistration(User.RegistrationRequest request, StreamObserver<User.RegistrationResponse> responseObserver) throws ClassNotFoundException, SQLException {
    Class.forName("com.mysql.cj.jdbc.Driver");
    int userID = request.getUserid();
    String name = request.getName();
    String country = request.getCountry();
    String email = request.getEmail();
    String username = request.getUsername();
    String pass = request.getPassword();

    ResultSet resultSet = checkPersonalInfo(userID);
    User.RegistrationResponse.Builder response = new User.RegistrationResponse.Builder();
    while (resultSet.next()){
        if(resultSet.getInt(1) == 1){
            response.setResponseMessage("User " + userID + " is already registered").setResponseCode(500);
        }
        else {
            Connection connection = getConnection(databaseURL, user, password);
            PreparedStatement statement = connection.prepareStatement
                    ("INSERT INTO user_tbl VALUES('"+userID+"', '"+name+"', '"+country+"')");
            statement.executeUpdate();
            PreparedStatement loginStatement = connection.prepareStatement("INSERT INTO user_login VALUES('"+username+"', '"+email+"', '"+pass+"')");
            loginStatement.executeUpdate();
            response.setResponseMessage(name +
                            " with User ID " + userID + " from " + country + " is now registered successfully").
                    setResponseCode(300);
        }
        break;
    }
    responseObserver.onNext(response.build());
    responseObserver.onCompleted();
}
    private ResultSet checkPersonalInfo(int userID) throws SQLException {
        Connection connection = getConnection(databaseURL, user, password);
        PreparedStatement statement = connection.prepareStatement("SELECT EXISTS(SELECT * FROM user_tbl WHERE user_ID = ?)");
        statement.setInt(1, userID);
        return statement.executeQuery();
    }

    private ResultSet checkLogInCredential(String userName, String password) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = getConnection(databaseURL, user, this.password);
        PreparedStatement statement = connection.prepareStatement("SELECT EXISTS(SELECT * FROM user_login " +
                " WHERE userName =  BINARY '"+userName+"' && password = BINARY '"+password+"')");

        return statement.executeQuery();
    }
}







