package pend.rubato.home.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import pend.rubato.home.dao.IDao;
import pend.rubato.home.dto.RFBoardDto;
import pend.rubato.home.dto.RReplyDto;

@Controller
public class HomeController {

	
	@Autowired
	private SqlSession sqlSession;
	
	
	
	
	@RequestMapping(value = "/")
	public String home() {
		return "redirect:index";
	}
	
	
	
	
	@RequestMapping(value = "index")
	public String index(Model model) {
		
		IDao dao = sqlSession.getMapper(IDao.class);
		
		List<RFBoardDto> boardDtos = dao.rfblist();	// 전체 글 리스트 불러오기 -> 어레이리스트보다 상위개념인 리스트로 받는다.

		int boardSize = boardDtos.size(); // 전체 글의 개수
		
		if(boardSize >= 4) {
			boardDtos = boardDtos.subList(0, 4);	
		} else {
			boardDtos = boardDtos.subList(0, boardSize+1);		
		}	// 전체 글의 개수가 4개보다 작을때 : 만약 작게되면 인덱스 에러가 발생한다. 이 구문은 그것을 방지한다.
		
		
		model.addAttribute("latestDtos", boardDtos);
		
		return "index";
	//		boardDtos.get(0); // 가장 최근 글 첫번째
//		boardDtos.get(1); // 가장 최근 글 두번째
//		boardDtos.get(2); // 가장 최근 글 세번째
//		boardDtos.get(3); // 가장 최근 글 네번째
//		굳이 따로 안써도되고 모델에 바로넣어버려도된다.	
//		ArrayList<RFBoardDto> latestDtos = null;

//		model.addAttribute("freeboard01", boardDtos.get(0));
//		model.addAttribute("freeboard02", boardDtos.get(1));
//		model.addAttribute("freeboard03", boardDtos.get(2));
//		model.addAttribute("freeboard04", boardDtos.get(3));
		

	}
	
	// 여러개를 리스트로 보내는 구문
	@RequestMapping(value = "board_list")
	public String board_list(Model model) {
		
		
		IDao dao = sqlSession.getMapper(IDao.class);
		
		ArrayList<RFBoardDto> boardDtos = dao.rfblist();
		int boardCount = dao.rfboardAllCount();
		
		model.addAttribute("boardList", boardDtos);
		model.addAttribute("boardCount", boardCount);
		
		
		return "board_list";
	}
	
	
	@RequestMapping(value = "delete")
	public String delete(HttpServletRequest request, Model model) {
		
		IDao dao = sqlSession.getMapper(IDao.class);
		
		String rfbnum = request.getParameter("rfbnum");
		
		dao.delete(rfbnum);
		
	
		return "redirect:board_list";
	}
	
	
	@RequestMapping(value = "board_view")
	public String board_view(HttpServletRequest request, Model model, HttpSession session) {
		
		
		IDao dao = sqlSession.getMapper(IDao.class);
		String sessionId = (String) session.getAttribute("memberId");
		String rfbnum = request.getParameter("rfbnum");
		//사용자가 글리스트에서 클릭한 글의 번호
		
		dao.rfbhit(rfbnum);	//조회수 증가
		
		RFBoardDto rfboardDto = dao.rfboardView(rfbnum);
		ArrayList<RReplyDto> replyDtos =  dao.rrlist(rfbnum);
		
		model.addAttribute("rfbView", rfboardDto);
		model.addAttribute("replylist", replyDtos);//해당 글에 달린 댓글 리스트
		
		
		
		 
		return "board_view";
	}
	
	
	@RequestMapping(value = "board_write")
	public String board_write(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		String sessionId = (String) session.getAttribute("memberId");
		if(sessionId == null) {		// 참이면 로그인이 안된 상태
			PrintWriter out;
		 try {
			response.setContentType("text/html;charset=utf-8");
			out = response.getWriter();
			out.println("<script>alert('로그인하지않으면 글을 쓰실수없습니다!');history.go(-1);</script");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
}
		
		return "board_write";
	}
	
	
	
	@RequestMapping(value = "member_join")
	public String member_join() {
		return "member_join";
	}
	
	
	
	
	@RequestMapping(value = "joinOk")
	public String joinOk(HttpServletRequest request, Model model, HttpSession session) {
		
		
		
		String memberId = request.getParameter("mid");
		String memberPw = request.getParameter("mpw");
		String memberName = request.getParameter("mname");
		String memberEmail = request.getParameter("memail");
		
		
		
		IDao dao = sqlSession.getMapper(IDao.class);
		
		dao.joinMember(memberId, memberPw, memberName, memberEmail);
		
		session.setAttribute("memberId", memberId);; // 가입과 동시에 로그인
		
		return "redirect:index";
	}
	
	
	@RequestMapping(value = "loginOk")
	public String loginOk(HttpServletRequest request, Model model, HttpSession session) {
		
		IDao dao = sqlSession.getMapper(IDao.class);
		
		String memberId = request.getParameter("mid");
		String memberPw = request.getParameter("mpw");
		
		int checkIdFlag = dao.checkUserIdAndPw(memberId,memberPw);
		
		
		// 세션이 있어야한다. 그러므로 바로 위에써도써서 불러오면된다. 세션에 값을 넣어야하므로 set을써야한다.
		if(checkIdFlag == 1) {
			session.setAttribute("memberId", memberId);
		}	// 로그인성공 
			
		
		return "redirect:index";
	}
	
	// 로그아웃 하는 맵핑
	@RequestMapping(value = "logout")
	public String logout(HttpSession session) {
		
		session.invalidate();
		
		
		return "redirect:index";
	}
	
	
	@RequestMapping(value = "writeOk")
	public String writeOk(HttpServletRequest request, Model model, HttpSession session, @RequestPart MultipartFile files) throws IllegalStateException, IOException {
		
		IDao dao = sqlSession.getMapper(IDao.class);
		
		String boardName = request.getParameter("rfbname");
		String boardTitle = request.getParameter("rfbtitle");
		String boardContent = request.getParameter("rfbcontent");
		
		
		String sessionId = (String) session.getAttribute("memberId");
		// 글쓴이 아이디는 현재 로그인 유저의 아이디 이므로  세션에서 가져와서 전달  rfbid == memberid
		
		
		if(files.isEmpty())	{ // 파일의 첨부여부 확인
			dao.rfbwrite(boardName, boardTitle, boardContent, sessionId);				
		} else {
			dao.rfbwrite(boardName, boardTitle, boardContent, sessionId);
			
			//파일첨부
		String filoriename = files.getOriginalFilename(); // 첨부된파일의 원래 이름
		String fileextension = FilenameUtils.getExtension(filoriename).toLowerCase();	//첨부된 파일의 확장자
		//첨부된 파일의 확장자 추출 후(FilenameUtils) 소문자로 강제 변경 toLowerCase()
		File destinationFile; // java.io 패키지 제공 클래스 임포트
		String destinationFileName; // 실제 서버에 저장된 파일의 변경된 이름이 저장될 변수 선언
		String fileurl ="D:/gyuseong/SpringBoot_warkspace/rubatoProject-2022.11.17-pend/src/main/resources/static/uploadfiles/";	// 서버의 절대경로로 써야한. 첨부된 파일이 저장될 서버의 실제 폴더 경로
		
		destinationFileName =  RandomStringUtils.randomAlphabetic(32) + "." + fileextension; 
		//알파벳대소문자와 숫자를 포함한 랜덤 32자 문자열 생성 후 .을 구분자로 원본 파일의 확장자를 연결->실제 서버에 저장될 파일의 이름
		destinationFile = new File(fileurl+destinationFileName); 
		
		destinationFile.getParentFile().mkdir();
		files.transferTo(destinationFile);
		
		
		
		
		}

		

		
		
		return "redirect:board_list";
	}
	
	
	
	
	@RequestMapping(value = "replyOk")
	public String replyOk(HttpServletRequest request, HttpSession session, Model model, HttpServletResponse response) {
		
		
		
		String rrorinum = request.getParameter("rfbnum"); // 댓글이 달린 원글의 번호
		String rrcontent = request.getParameter("rrcontent");	// 댓글 내용
		
		String sessionId = (String) session.getAttribute("memberId");
		
		if(sessionId == null) {		// 참이면 로그인이 안된 상태
			PrintWriter out;
		 try {
			response.setContentType("text/html;charset=utf-8");
			out = response.getWriter();
			out.println("<script>alert('로그인하지않으면 글을 쓰실수없습니다!');history.go(-1);</script");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		} else {
			
			IDao dao = sqlSession.getMapper(IDao.class);
			dao.rrwrite(rrorinum, sessionId, rrcontent);//댓글 쓰기
			dao.rrcount(rrorinum);//해당글의 댓글 총 개수 증가
			
			RFBoardDto rfboardDto = dao.rfboardView(rrorinum);
			ArrayList<RReplyDto> replyDtos =  dao.rrlist(rrorinum);
			
			model.addAttribute("rfbView", rfboardDto);//원글의 게시글 내용 전부
			model.addAttribute("replylist", replyDtos);//해당 글에 달린 댓글 리스트
			
		}
		
		
		
		return "board_view";
	}
	
	
	
	@RequestMapping(value = "replyDelete")
	public String replyDelete(HttpServletRequest request, HttpSession session, Model model) {
		
		String rrnum = request.getParameter("rrnum");//댓글 고유번호
		String rrorinum = request.getParameter("rfbnum");//댓글이 달린 원글의 고유번호
		
		IDao dao = sqlSession.getMapper(IDao.class);
		
		dao.rrdelete(rrnum);//댓글 삭제
		dao.rrcountMinus(rrorinum);//해당 글의 댓글 갯수 1감소
		
		RFBoardDto rfboardDto = dao.rfboardView(rrorinum);
		ArrayList<RReplyDto> replyDtos =  dao.rrlist(rrorinum);
		
		model.addAttribute("rfbView", rfboardDto);//원글의 게시글 내용 전부
		model.addAttribute("replylist", replyDtos);//해당 글에 달린 댓글 리스트
		
		
		return "board_view";
	}
	
	@RequestMapping(value = "search_list")
	public String search_list(HttpServletRequest request, HttpSession session, Model model) {
		
		IDao dao = sqlSession.getMapper(IDao.class);
		
		String searchOption = request.getParameter("searchOption");
		// title, content, writer 3개중 한개의 값을 저장
		String searchKey = request.getParameter("searchKey");
		// 사용유저가 입력한 제목,내용,글쓴이 에 포함된 검색 키워드 낱말
		
		ArrayList<RFBoardDto> boardDtos = null;	//3 개다 거짓일수도있기에 미리 null값을준다.
		
		if(searchOption.equals("title")) { // 검색을하고 if ~ else if 에서 찾는다. 찾은 후 boarddots를 생성
			 boardDtos = dao.rfbSearchTitleList(searchKey);
		} else if(searchOption.equals("content")) {
			boardDtos = dao.rfbSearchContentList(searchKey);
		} else if(searchOption.equals("writer")) {
			boardDtos = dao.rfbSearchWriterList(searchKey);
		}
		
		model.addAttribute("boardList", boardDtos);
		model.addAttribute("boardCount", boardDtos.size());// 검색 결과 게시물의 개수 반환
		
		
		
		
		return "board_list";
	}
	
}
