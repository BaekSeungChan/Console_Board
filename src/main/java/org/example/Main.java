package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        UserRepository user  = new UserRepository();

        Scanner sc = new Scanner(System.in);

        while (true){
            System.out.println("=====================================");
            System.out.println("1. 회원가입");
            System.out.println("2. 로그인");
            System.out.println("3. 아이디 찾기");
            System.out.println("4. 비밀번호 초기화");
            System.out.println("5. 프로그램 종료");
            System.out.println();
            System.out.print("원하는 기능?");

            int numChoice = sc.nextInt();
            sc.nextLine();

            switch (numChoice) {
                case 1:
                    user.signUp();
                    break;
                case 2:
                    user.login();
                    break;
                case 5:
                    System.out.println("프로그램을 종료합니다.");
                    System.exit(0);
                    break;
                default:
            }
        }
    }
}