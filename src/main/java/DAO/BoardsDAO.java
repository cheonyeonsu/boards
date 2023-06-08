package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import DTO.Boards;

public class BoardsDAO {

	Connection conn = null; // 데이터베이스의 연결을 담당
	PreparedStatement pstmt; // 쿼리문의 실행을 담당

	final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
	final String JDBC_URL = "jdbc:oracle:thin:@localhost:1521:xe";

	// db연결 메소드 [스튜던트랑 다르니까 수정]
	public Connection open() {
		Connection conn = null;
		try {
			Class.forName(JDBC_DRIVER); // 드라이버 로드
			conn = DriverManager.getConnection(JDBC_URL, "test", "test1234"); // db연결. 내 db랑 계정 맞추기.

		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	// 게시판 리스트 가져오기
	public ArrayList<Boards> getList() throws SQLException {
		Connection conn = open();
		ArrayList<Boards> boardsList = new ArrayList<>(); // ArrayList 스팰링 주의

		String sql = "select boards_no, title, user_id, to_char(reg_date,'yyyy.mm.dd') reg_date, views from boards";
		PreparedStatement pstmt = conn.prepareStatement(sql); // 쿼리문 등록. add throws해줌
		ResultSet rs = pstmt.executeQuery();

		try (conn; pstmt; rs) {
			while (rs.next()) { // rs.next() : 한 라인씩 데이터를 읽어온다.
				Boards b = new Boards();
				b.setBoards_no(rs.getInt("boards_no"));
				b.setTitle(rs.getString("title"));
				b.setUser_id(rs.getNString("user_id"));
				b.setReg_date(rs.getString("reg_date"));
				b.setViews(rs.getInt("views"));

				boardsList.add(b);
			}

			return boardsList;
		}
	}
	// 쿼리문 실행(무조건 트라이 캐치 써주기)

	// 게시물 내용 가져오기
	public Boards getView(int boards_no) throws Exception {
		Connection conn = open();
		Boards b = new Boards();

		String sql = "select boards_no, title, user_id,to_char(reg_date,'yyyy.mm.dd') "
				+ "reg_date,views, content, img from boards where boards_no = ?"; // 계속 바뀌어서 ?사용 > 따로 지정 필.
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, boards_no); // 첫번째 물음표에 보드넘버를 넣겠다.
		ResultSet rs = pstmt.executeQuery();

		try (conn; pstmt; rs) {
			while (rs.next()) {
				b.setBoards_no(rs.getInt("boards_no"));
				b.setTitle(rs.getString("title"));
				b.setUser_id(rs.getNString("user_id"));
				b.setReg_date(rs.getString("reg_date"));
				b.setViews(rs.getInt("views"));
				b.setContent(rs.getString("content"));
				b.setImg(rs.getString("img"));
			}
			return b;
		}
	}

	// 조회수 증가
	public void updateViews(int boards_no) throws SQLException {
		Connection conn = open();
		String sql = "update boards set views = (views + 1) where boards_no = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);

		try (conn; pstmt) {
			pstmt.setInt(1, boards_no);
			pstmt.executeUpdate(); // 얘를 해줘야 실행이 됨~!~!~
		}
	}

	// 게시글 등록
	public void insertBoards(Boards b) throws SQLException {
		Connection conn = open();
		String sql = "insert into boards(boards_no, user_id, title, content, reg_date, views, img)"
				+ " values(boards_seq.nextval, ?, ?, ?, sysdate, 0, ?)";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		try (conn; pstmt) {
			pstmt.setString(1, b.getUser_id());
			pstmt.setString(2, b.getTitle());
			pstmt.setString(3, b.getContent());
			pstmt.setString(4, b.getImg());
			pstmt.executeUpdate();
		}
	}

	// 게시글 수정화면 보여주기.
	public Boards getViewForEdit(int boards_no) throws SQLException {
		Connection conn = open();
		Boards b = new Boards();
		String sql = "select boards_no, title, user_id,to_char(reg_date,'yyyy.mm.dd') "
				+ "reg_date,views, content, img from boards where boards_no = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, boards_no);
		ResultSet rs = pstmt.executeQuery();

		try (conn; pstmt; rs) {
			while (rs.next()) {
				b.setBoards_no(rs.getInt("boards_no"));
				b.setTitle(rs.getString("title"));
				b.setUser_id(rs.getNString("user_id"));
				b.setReg_date(rs.getString("reg_date"));
				b.setViews(rs.getInt("views"));
				b.setContent(rs.getString("content"));
				b.setImg(rs.getString("img"));
			}
			return b;

		}
	}

	// 게시글 수정하기
	public void updateBoards(Boards b) throws Exception {
		Connection conn = open();

		String sql = "update boards set title = ?, user_id = ?, content = ?, img = ? where boards_no = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);

		try (conn; pstmt) {
			pstmt.setString(1, b.getTitle());
			pstmt.setString(2, b.getUser_id());
			pstmt.setString(3, b.getContent());
			pstmt.setString(4, b.getImg());
			pstmt.setInt(5, b.getBoards_no());

			// 수정된 글이 없을 경우
			if (pstmt.executeUpdate() != 1) {
				throw new Exception("수정된 글이 없습니다.");
			}
		}

	}

	// 게시글 삭제
	public void deleteBoards(int boards_no) throws Exception {
		Connection conn = open();
		String sql = "delete from boards where boards_no=?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		
		try(conn; pstmt){
			pstmt.setInt(1, boards_no);
			if (pstmt.executeUpdate() !=1) {
				throw new Exception("삭제된 글이 없습니다.");
			}
		}
	}

}
