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
}
