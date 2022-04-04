import com.demo.grpc.User;
import com.demo.grpc.userGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Scanner;

public class Client {
    public static void main(String[] args){
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost",2047).usePlaintext().build();
        userGrpc.userBlockingStub userBlockingStub = new userGrpc.userBlockingStub(managedChannel);
        String userName, password;
        boolean isAuthorized = false;
        Scanner input = new Scanner(System.in);
        System.out.println("new user?? Y/N");
        String userChoice = input.next();
        if(userChoice.equals("Y")) {
            register(userBlockingStub);
        }
        while (!isAuthorized){
            System.out.println("Please enter the userHandle ");
            userName = input.next();
            System.out.println("plese enter your password");
            password = input.next();
            User.LoginRequest loginRequest = User.LoginRequest.newBuilder().setUsername(userName).setPassword(password).build();
            User.LoginResponse loginResponse = userBlockingStub.login(loginRequest);
            if(loginResponse.getResCode() == 200){
                isAuthorized = true;
            }
            System.out.println(loginResponse.getMessage());
        }
    }

    public static void register(userGrpc.userBlockingStub userBlockingStub) {

        Scanner input = new Scanner(System.in);

        System.out.println("Enter Your UserID please: ");
        int userID = Integer.parseInt(input.nextLine());

        System.out.println("Enter Your name please: ");
        String name_registration = input.nextLine();

        System.out.println("Enter Your country please: ");
        String country = input.nextLine();
        System.out.println("Enter Your email please: ");
        String email = input.nextLine();
        System.out.println("Please enter the userHandle ");
        String userName = input.next();
        System.out.println("plese enter your password");
        String password = input.next();


        User.RegistrationRequest registrationRequest = User.RegistrationRequest.newBuilder().setUserid(userID).setName(name_registration)
                .setCountry(country).setEmail(email).setUsername(userName).setPassword(password).build();
        User.RegistrationResponse registrationResponse = userBlockingStub.userRegistration(registrationRequest);
        System.out.println(registrationResponse.getResponseMessage());

    }

}
