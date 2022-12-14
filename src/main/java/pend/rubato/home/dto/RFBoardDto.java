package pend.rubato.home.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;




@Data
@NoArgsConstructor
@AllArgsConstructor
public class RFBoardDto {

	private int rfbnum;
	private String rfbname;
	private String rfbtitle;
	private String rfbcontent;
	private int rfbhit;	//조휘수
	private String rfbuserid;	// 글쓴이의 아이디
	private int rfbreplycount; // 첨부된 댓글 개수
	private String rfbdate;
	private int filecount;	// 첨부된 파일 개수
	
	
	
	
	
	
	
	
}
