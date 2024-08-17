package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class UserRepository {
    Scanner sc = new Scanner(System.in);
    Board board = new Board();

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

    public void login(){

        String username = "";
        String password = "";

        while (true){
            System.out.print("아이디: ");
            username = sc.nextLine();

            System.out.print("비밀번호: ");
            password = sc.nextLine();

            System.out.println("1. 로그인");
            System.out.println("2. 다시입력");
            System.out.println("3. 이전화면으로");

            System.out.print("원하는 기능? ");

            int numChoice = sc.nextInt();
            sc.nextLine();

            switch (numChoice){
                case 1:
                    if (loginUser(username, password)) {
                        recordLogin(username);
                        showMenu(username);
                        return;
                    } else {
                        System.out.println("로그인 실패. 아이디 또는 비밀번호를 확인하세요.");
                    }
                    break;
                case 2:
                    break;
                case 3:
                    return;
                default:
                    System.out.println("1, 2, 3 중 선택하세요.");
            }
        }
    }

    private void recordLogin(String username) {
        PreparedStatement pstmt = null;
        String insertHistorySql = "INSERT INTO login_history (username, login_time) VALUES ( ?, NOW())";
        String updateUserLogin = "UPDATE users SET last_login = NOW() WHERE username = ?";

        try(Connection conn = DBConnection.getConnection()){
            pstmt = conn.prepareStatement(insertHistorySql);
            pstmt.setString(1, username);
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement(updateUserLogin);
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void showMenu(String username) {
        int numChoice = 0;

        do{
            System.out.println("1. 나의 정보 확인");
            System.out.println("2. 게시물 목록");
            System.out.println("3. 게시물 등록");
            System.out.println("4. 로그아웃");
            System.out.print("원하는 기능 > ");
            numChoice = sc.nextInt();
            sc.nextLine();

            switch (numChoice){
                case 1:
                    showUserInfo(username);
                    break;
                case 2:
                    board.boardList();
                    break;
                case 3:
                    board.addPost(username);
                    break;
                case 4:
                    recordLogout(username);
                    System.out.println("로그아웃 성공!");
                    return;
                default:
                    System.out.println("1, 2, 3 중 선택하세요.");
            }

        } while (numChoice != 4);
    }

    private void showUserInfo(String username) {
        String selectUserQuery = "SELECT * FROM users WHERE username =?";
        String password = "";

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(selectUserQuery);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("-----------------------------");
                System.out.printf("아이디: %s\n", rs.getString("username"));
                System.out.printf("이름: %s\n", rs.getString("name"));
                System.out.printf("전화번호: %s\n", rs.getString("phone"));
                System.out.printf("주소: %s\n", rs.getString("address"));
                System.out.printf("성별: %s\n", rs.getString("gender"));
                password = rs.getString("password");
            }

            System.out.println("1. 회원 정보 수정");
            System.out.println("2. 회원 탈퇴");
            System.out.println("3. 뒤로 가기");
            System.out.print("원하는 기능 > ");

            int numChoice = 0;
            numChoice = sc.nextInt();
            sc.nextLine();

            switch (numChoice){
                case 1:
                    System.out.print("비밀번호 입력해주세요: ");
                    String passwordConfirm = sc.nextLine();
                    if(password.equals(passwordConfirm)){
                        updateUserInfo(username);
                    } else {
                        System.out.println("비밀번호가 일치하지 않습니다.");
                    }
                    break;
                case 2:
                    deleteUser(username);
                    return;
                default:
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteUser(String username) {
        String selectUserQuery = "SELECT * FROM users WHERE username = ?";
        String insertDeleteUserQuery = "INSERT INTO deleted_users (user_id, username, password, name, phone, address, gender) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String updateDeleteUserQuery = "UPDATE users SET is_deleted = TRUE WHERE username = ?";
        PreparedStatement pstmt = null;

        try (Connection conn = DBConnection.getConnection()) {
            pstmt = conn.prepareStatement(selectUserQuery);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                PreparedStatement insertPstmt = conn.prepareStatement(insertDeleteUserQuery);
                insertPstmt.setInt(1, rs.getInt("user_id"));
                insertPstmt.setString(2, rs.getString("username"));
                insertPstmt.setString(3, rs.getString("password"));
                insertPstmt.setString(4, rs.getString("name"));
                insertPstmt.setString(5, rs.getString("phone"));
                insertPstmt.setString(6, rs.getString("address"));
                insertPstmt.setString(7, rs.getString("gender"));
                insertPstmt.executeUpdate();

                PreparedStatement deletePstmt = conn.prepareStatement(updateDeleteUserQuery);
                deletePstmt.setString(1, username);
                deletePstmt.executeUpdate();

                System.out.println("회원 탈퇴가 완료되었습니다.");
            } else {
                System.out.println("존재하지 않는 회원입니다.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateUserInfo(String username) {
        PreparedStatement pstmt = null;

        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("-----------------------------");
            System.out.println("변경할 정보를 입력하세요.");

            System.out.print("이름: ");
            String name = sc.nextLine();

            System.out.print("전화번호: ");
            String phone = sc.nextLine();

            System.out.print("주소: ");
            String address = sc.nextLine();

            System.out.println("1. 회원 정보 수정");
            System.out.println("2. 취소");
            System.out.print("원하는 기능 > ");

            int numChoice = 0;

            numChoice = sc.nextInt();
            sc.nextLine();

            if(numChoice == 1){
                String updateUserSql = "UPDATE users SET name =?, phone =?, address =? WHERE username = ?";
                pstmt = conn.prepareStatement(updateUserSql);
                pstmt.setString(1, name);
                pstmt.setString(2, phone);
                pstmt.setString(3, address);
                pstmt.setString(4, username);

                int result = pstmt.executeUpdate();

                if (result > 0) {
                    System.out.println("회원 정보가 수정되었습니다.");
                }
            } else {
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean loginUser(String username, String password){
        String selectUserQuery = "SELECT * FROM users WHERE username = ?";
        try(Connection conn = DBConnection.getConnection()){
            PreparedStatement pstmt = conn.prepareStatement(selectUserQuery);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                if(rs.getString("password").equals(password)){
                    System.out.println("로그인 성공!");
                    return true;
                } else {
                    System.out.println("비밀번호가 일치하지 않습니다.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    private void recordLogout(String username) {
        PreparedStatement pstmt = null;

        String logoutSql = "UPDATE login_history SET logout_time = NOW() WHERE username = ?";
        String userLogoutSql = "UPDATE users SET last_logout = NOW() WHERE username = ?";

        try(Connection conn = DBConnection.getConnection()){
            pstmt = conn.prepareStatement(logoutSql);
            pstmt.setString(1, username);
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement(userLogoutSql);
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
