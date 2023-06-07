package conrtoller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DAO.BoardsDAO;
import DTO.Boards;

@WebServlet("/") // 디폴트 서블릿
public class Boardscontroller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BoardsDAO dao;
	private ServletContext ctx; // 자원관리. 페이지 이동. 포워드를 위해 사용

	public Boardscontroller() {
		super();

	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		dao = new BoardsDAO();
		ctx = getServletContext();
	}

	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8"); // 한글 깨짐 방지.
		String command = request.getServletPath(); // 경로를 가지고 온다.
		String site = null;
		System.out.println("command: " + command);

		// 1. 경로지정 : 라우팅
		switch (command) {
		case "/list":
			site = getList(request);
			break;
		}
		
		
		ctx.getRequestDispatcher("/"+site).forward(request, response);
		
	}

	public String getList(HttpServletRequest request) {
		ArrayList<Boards> list;

		try {
			list = dao.getList();
			request.setAttribute("boardsList", list);
		} catch (SQLException e) {

			e.printStackTrace();
		}

		return "index.jsp";
	}

}
