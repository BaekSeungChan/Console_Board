package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class UserRepository {
    Scanner sc = new Scanner(System.in);

    public void signUp(){
        String username = "";
        String password = "";
        String name = "";
        String phone = "";
        String address = "";
        String gender = "";

        while(true){
            System.out.println("-----------------------------");
            System.out.println("회원 가입화면");

            System.out.print("아이디: ");
            username = sc.nextLine();

            System.out.print("비밀번호: ");
            password = sc.nextLine();

            System.out.print("이름: ");
            name = sc.nextLine();

            System.out.print("전화번호: ");
            phone = sc.nextLine();

            System.out.print("주소: ");
            address = sc.nextLine();

            System.out.print("성별 (M/F): ");
            gender = sc.nextLine();
            System.out.println("-----------------------------");


            System.out.println("1. 가입");
            System.out.println("2. 다시입력");
            System.out.println("3. 이전 화면으로");
            System.out.print("원하는 기능? ");

            int numChoice = sc.nextInt();
            sc.nextLine();

            switch (numChoice){
                case 1:
                    insertUser(username, password, name, phone, address, gender);
                    return;
                case 2:
                    break;
                case 3:
                    return;
                default:
                    System.out.println("1, 2, 3 중 선택하세요.");
            }
        }
    }

    public void loginUser(String username, String password){

    }

    private void insertUser(String username, String password, String name, String phone, String address, String gender){
        String insertUserQuery = "INSERT INTO users (username, password, name, phone, address, gender) VALUES(?, ?, ?, ?, ?, ?)";

        try(Connection conn = DBConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(insertUserQuery);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, name);
            pstmt.setString(4, phone);
            pstmt.setString(5, address);
            pstmt.setString(6, gender);

            int resultNum = pstmt.executeUpdate();

            if(resultNum > 0){
                System.out.println("가입을 축하합니다.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
