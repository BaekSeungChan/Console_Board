package org.example;

import java.sql.*;
import java.util.Scanner;

public class Board {

    Scanner sc = new Scanner(System.in);

    public void boardList(){
        int currentPage = 1;
        int pageSize = 10;
        boolean exit = false;


        while(!exit){
            showPageList(currentPage, pageSize);

            System.out.println("1. 이전 페이지(P), 2. 다음 페이지(N), 3. 상세 페이지 이동(숫자), 4. 나가기");
            System.out.print("원하는 기능 > ");
            String numChoice = sc.nextLine().toUpperCase();

            switch (numChoice){
                case "P":
                    if(currentPage > 1){
                        currentPage--;
                    } else {
                        System.out.println("이전 페이지가 없습니다.");
                    }
                    break;
                case "N":
                    currentPage++;
                    break;
                case "3":
                    System.out.print("페이지 번호 > ");
                    int postId = sc.nextInt();
                    sc.nextLine();
                    showPostDetail(postId);
                    break;
                case "4":
                    exit = true;
                    break;
                default:
                    try{
                        int targetPage = Integer.parseInt(numChoice);
                        if(targetPage > 0){
                            currentPage = targetPage;
                        } else {
                            System.out.println("잘못된 페이지 번호입니다.");
                        }
                    } catch (Exception e) {
                        System.out.println("잘못된 입력입니다. " + e);
                    }
                    break;
            }
        }

    }

    private void showPostDetail(int postId) {
        PreparedStatement pstmt = null;
        String selectPostQuery = "SELECT * FROM posts WHERE post_id = ?";
        String updateViewCountQuery = "UPDATE posts SET view_count = view_count + 1 WHERE post_id = ?";

        try(Connection conn = DBConnection.getConnection()) {
            pstmt = conn.prepareStatement(updateViewCountQuery);
            pstmt.setInt(1, postId);
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement(selectPostQuery);
            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("----------------------------");
                System.out.println("제목: " + rs.getString("title"));
                System.out.println("작성자: " + rs.getString("author"));
                System.out.println("내용: " + rs.getString("content"));
                System.out.println("조회수: " + rs.getInt("view_count"));
                System.out.println("작성일: " + rs.getTimestamp("created_at"));
            } else {
                System.out.println("해당 게시물이 존재하지 않습니다.");
            }

            System.out.println("----------------------------");
            System.out.println("1. 목록으로 돌아가기");
            System.out.println("2. 게시물 수정");
            System.out.println("3. 게시물 삭제");
            System.out.print("선택 > ");
            int numChoice = 0;
            numChoice = sc.nextInt();
            sc.nextLine();

            switch (numChoice){
                case 1:
                    break;
                case 2:
                    updatePost(postId);
                    break;
                case 3:
                    deletePost(postId);
                    break;
                default:
                    System.out.println("잘못된 선택입니다.");
                    break;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void updatePost(int postId) {
        PreparedStatement pstmt = null;
        String selectPostQuery = "SELECT * FROM posts WHERE post_id = ?";
        String updatePostQuery = "UPDATE posts SET title = ?, content = ? WHERE post_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            pstmt = conn.prepareStatement(selectPostQuery);
            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String correctPassword = rs.getString("password");
                boolean passwordMatched = false;
                int failCount = 0;

                while (!passwordMatched && failCount < 3) {
                    System.out.print("비밀번호를 입력해주세요 > ");
                    String passwordConfirm = sc.nextLine();

                    if (correctPassword.equals(passwordConfirm)) {
                        passwordMatched = true;

                        // 수정할 제목과 내용을 입력받음
                        System.out.print("변경할 제목: ");
                        String updatedTitle = sc.nextLine();
                        System.out.print("변경할 내용: ");
                        String updatedContent = sc.nextLine();

                        pstmt = conn.prepareStatement(updatePostQuery);
                        pstmt.setString(1, updatedTitle);
                        pstmt.setString(2, updatedContent);
                        pstmt.setInt(3, postId);
                        pstmt.executeUpdate();
                        System.out.println("게시물이 수정되었습니다.");
                    } else {
                        failCount++;  // 비밀번호가 틀리면 횟수를 증가
                        if (failCount < 3) {
                            System.out.println("비밀번호가 틀렸습니다. 다시 시도해주세요.");
                        } else {
                            System.out.println("비밀번호를 3회 틀리셨습니다. 목록으로 돌아갑니다.");
                        }
                    }
                }
            } else {
                System.out.println("해당 게시물이 존재하지 않습니다.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void deletePost(int postId) {
        PreparedStatement pstmt = null;
        String selectPostQuery = "SELECT * FROM posts WHERE post_id = ?";
        String deletePostQuery = "DELETE FROM posts WHERE post_id = ?";

        try(Connection conn = DBConnection.getConnection()) {
            pstmt = conn.prepareStatement(selectPostQuery);
            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String password = rs.getString("password");
                boolean passwordMatched = false;
                int failCount = 0;

                while (!passwordMatched && failCount < 3) {
                    System.out.print("비밀번호를 입력해주세요: ");
                    String passwordConfirm = sc.nextLine();

                    if (password.equals(passwordConfirm)) {
                        passwordMatched = true;
                        pstmt = conn.prepareStatement(deletePostQuery);
                        pstmt.setInt(1, postId);
                        pstmt.executeUpdate();
                        System.out.println("게시물이 삭제되었습니다.");
                    } else {
                        failCount++;
                        if (failCount < 3) {
                            System.out.println("비밀번호가 일치하지 않습니다. 다시 시도해주세요.");
                        } else {
                            System.out.println("비밀번호를 3회 틀리셨습니다. 목록으로 돌아갑니다.");
                        }
                    }
                }
            } else {
                System.out.println("해당 게시물이 존재하지 않습니다.");
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void showPageList(int currentPage, int pageSize){
        int offset = (currentPage - 1) * 10;
        PreparedStatement pstmt = null;
        String selectPageQuery = "SELECT * FROM posts ORDER BY created_at DESC LIMIT ?, ?";

        try(Connection conn = DBConnection.getConnection()) {
            pstmt = conn.prepareStatement(selectPageQuery);
            pstmt.setInt(1, offset);
            pstmt.setInt(2, pageSize);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("게시물 번호 | 작성자 | 제목 | 읽은수 | 작성일");
            System.out.println("----------------------------------------");


            while (rs.next()){
                int postId = rs.getInt("post_id");
                String author = rs.getString("author");
                String title = rs.getString("title");
                int viewCount = rs.getInt("view_count");
                Timestamp createdAt = rs.getTimestamp("created_at");

                System.out.printf("%d | %s | %s | %d | %s\n", postId, author, title, viewCount, createdAt);
            }

            System.out.println("현재 페이지: " + currentPage);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addPost(String username) {
        String callProcedure = "{CALL AddPost(?,?,?,?,?)}";

        try(Connection conn = DBConnection.getConnection()) {
            CallableStatement cstmt = conn.prepareCall(callProcedure);

            System.out.print("제목: ");
            String title = sc.nextLine();
            System.out.print("내용: ");
            String content = sc.nextLine();
            System.out.print("비밀번호 (수정/삭제 시 사용): ");
            String password = sc.nextLine();

            Timestamp currentTime = new Timestamp(System.currentTimeMillis());

            cstmt.setString(1, username);
            cstmt.setString(2, title);
            cstmt.setString(3, content);
            cstmt.setString(4, password);
            cstmt.setTimestamp(5, currentTime);

            cstmt.executeUpdate();
            System.out.println("게시물이 등록되었습니다.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
