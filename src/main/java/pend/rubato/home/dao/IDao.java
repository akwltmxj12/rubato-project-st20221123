package pend.rubato.home.dao;

import java.util.ArrayList;

import pend.rubato.home.dto.FileDto;
import pend.rubato.home.dto.RFBoardDto;
import pend.rubato.home.dto.RMemberDto;
import pend.rubato.home.dto.RReplyDto;

public interface IDao {

	// member 관련
	public void joinMember(String mid, String mpw, String mname, String memail);	// insert
	public int checkUserId(String mid);	// select문으로 아이디를 검사한다.
	public int checkUserIdAndPw(String mid, String mpw);	// select
	
	// 게시판 관련
	public void rfbwrite(String rfbname, String rfbtitle, String rfbcontent, String rfbuserid, int filecount);	// insert
	public ArrayList<RFBoardDto> rfblist();	// 게시판 list, select
	public int rfboardAllCount();	// 총 게시물 갯수 select
	public RFBoardDto rfboardView(String rfbnum); // 게시물 내용 보기, 클릭한 게시물의 번호, select문 써야한다. 파라미터값이 넘어오면 문자열로 넘어온다. 그래서 매게변수가 String이된다.
	public void delete(String rfbnum);	//글삭제	delete
	public void rfbhit(String rfbnum);	// 조회수 updata
	
	//댓글관련
	public void rrwrite(String rrorinum, String rrid, String rrcontent);//새 댓글 입력 insert
	public ArrayList<RReplyDto> rrlist(String rrorinum);//해당글의 댓글 리스트 select
	public void rrcount(String rrorinum);//댓글 등록시 해당글의 댓글갯수 1증가 
	public void rrdelete(String rrnum);	// 댓글 삭제 delete
	public void rrcountMinus(String rrorinum); // 댓글 삭제시 해당글의 댓글 갯수 1감소
	
	
	//게시판 검색 관련
	public ArrayList<RFBoardDto> rfbSearchTitleList(String searchKey);	// 제목에서 찾기
	public ArrayList<RFBoardDto> rfbSearchContentList(String searchKey);	// 내용에서 찾기
	public ArrayList<RFBoardDto> rfbSearchWriterList(String searchKey);	// 글쓴이 에서 찾기
	
	// 파일 업로드 관련
	public void fileInfoInsert(int boardnum, String fileoriname, String filename, String fileextension, String fileurl);
	public ArrayList<RFBoardDto> boardLatestInfo(String rfbuserid);
	//현재 파일이 첨부된 글로 쓴 아이디로 검색된 글 목록
	public FileDto getFileInfo(String rfbnum);	//파일이 첨부된 게시글의 번호로 조회환 첨부된 파일의 모든 정보 dto
	
	
	
}
