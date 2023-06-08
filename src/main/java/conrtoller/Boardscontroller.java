package conrtoller;


import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.beanutils.BeanUtils;

import DAO.BoardsDAO;
import DTO.Boards;

@WebServlet("/") // 디폴트 서블릿
@MultipartConfig(maxFileSize=1024*1024*2, location="c:/Temp/img")
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
		case "/list":site = getList(request);break;
		case "/view":site = getView(request);break;
		case "/write":site = "write.jsp";break; //글쓰기 화면을 보여줌
		case "/insert":site = insertBoards(request);break; //글 등록
		case "/edit":site = getViewForEdit(request);break; //수정 화면 보여줌
		case "/update":site = updateBoards(request); break; //글 수정
		case "/delete":site = deleteBoards(request); break; //글 삭제
		}
		
		/* 둘 다 새로운 페이지로 이동
		 *  redirect : 데이터를 가지고 이동하지 않음. 주소가 변한다.
		 *  > DB에 변화가 생기는 요청(글쓰기, 회원가입)
		 *  insert,update,delete
		 *  
		 * forward : 데이터를 가지고 있동. 주소가 변하지 않는다. 
		 * > 단순조회(상세페이지 보기, 리스트보기, 검색)
		 * select
		 * 
		  **/ 
		
		
		
		if(site.startsWith("redirect:/")) { //redirect
			String rview = site.substring("redirect:/".length());
			System.out.println("rview: " + rview);
			
			response.sendRedirect(rview); //페이지 이동
		} else {
			ctx.getRequestDispatcher("/"+site).forward(request, response);
			
		}
		
		
	}

	public String getList(HttpServletRequest request) {
		ArrayList<Boards> list;

		try {
			list = dao.getList();
			request.setAttribute("boardsList", list);
		} catch (Exception e) {

			e.printStackTrace();
		}

		return "index.jsp";
	}
	
	public String updateBoards(HttpServletRequest request) {
		Boards b = new Boards();
		try {
			BeanUtils.populate(b, request.getParameterMap());
			
			// 1. 이미지 파일 자체를 서버에 저장
			Part part = request.getPart("file"); // 이미지파일 받기
			String fileName = getFileName(part); // 파일 이름 가져오기
			
			if(fileName != null && !fileName.isEmpty()) {
				part.write(fileName); // 파일을 서버에 저장
			}
			// 2. 이미지 파일 이름에 "/img/" 경로룰 붙여서 board 객체에 저장
			b.setImg("/img/"+ fileName);
			
			dao.updateBoards(b);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "redirect:/view?boards_no=" +b.getBoards_no();
	}
	
	public String deleteBoards(HttpServletRequest request) {
		int boards_no = Integer.parseInt(request.getParameter("boards_no"));
		return "redirect:/list";
		
		try { //여기부분 부터 0609 시작
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	//파일 이름 추출
	public String getView(HttpServletRequest request) {
		int boards_no = Integer.parseInt(request.getParameter("boards_no"));
		try {
			dao.updateViews(boards_no); //조회수 증가.
			Boards b = dao.getView(boards_no); 
			request.setAttribute("boards", b);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "view.jsp";
	}
	
	public String insertBoards(HttpServletRequest request) {
		Boards b = new Boards();
		
		try {
			BeanUtils.populate(b, request.getParameterMap());
			//1. 이미지 파일 자체를 서버 컴퓨터에 저장
			Part part = request.getPart("file"); //이미지 파일 받기
			String fileName = getFileName(part); //파일 이름 구하기
			
			if(fileName != null && !fileName.isEmpty()) {
				part.write(fileName); //파일을 컴퓨터에 저장한다. 
			}
			
			//2. 이미지 파일 이름을 "/img/"경로를 붙여서 boards객체에 저장
			b.setImg("/img/" + fileName);
			
			dao.insertBoards(b);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/list";
		}
	
	public String getViewForEdit(HttpServletRequest request) {
		int boards_no = Integer.parseInt(request.getParameter("boards_no"));
		try {
			Boards b = dao.getViewForEdit(boards_no);
			request.setAttribute("boards", b);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "edit.jsp";
	}
	
	public String getFileName(Part part) {
		String fileName = null;
		String header= part.getHeader("content-disposition");
		System.out.println("header = >" + header);
		
		int start = header.indexOf("filename=");
		fileName = header.substring(start + 10, header.length() -1);
		System.out.println("파일명: " + fileName);
		
		return fileName;
	}
	
	
}
