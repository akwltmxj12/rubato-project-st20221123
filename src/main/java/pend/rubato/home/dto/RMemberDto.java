package pend.rubato.home.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RMemberDto {
	
	private String mid;
	private String mpw;
	private String mname;
	private String memail;
	private String mdate;
	
}