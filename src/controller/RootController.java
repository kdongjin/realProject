package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Student;

public class RootController implements Initializable {
	@FXML
	private TableView tableView;
	@FXML
	private TextField txtName;
	@FXML
	private ComboBox cmbLevel;
	@FXML
	private TextField txtBan;
	@FXML
	private RadioButton rdoMale;
	@FXML
	private RadioButton rdoFemale;
	@FXML
	private Button btnTotal;
	@FXML
	private Button btnAvg;
	@FXML
	private Button btnInit;
	@FXML
	private Button btnOk;
	@FXML
	private Button btnExit;
	@FXML
	private Button btnList;
	@FXML
	private TextField txtKo;
	@FXML
	private TextField txtEng;
	@FXML
	private TextField txtMath;
	@FXML
	private TextField txtSic;
	@FXML
	private TextField txtSoc;
	@FXML
	private TextField txtMusic;
	@FXML
	private TextField txtTotal;
	@FXML
	private TextField txtAvg;
	@FXML
	private TextField txtSearch;
	@FXML
	private Button btnSearch;
	@FXML
	private Button btnEdit;
	@FXML
	private Button btnDelete;
	@FXML
	private Button btnBarChart;


	public Stage stage;
	private ObservableList<Student> obsList;
	private ToggleGroup group;
	private int tableViewSelectedIndex;

	public RootController() {
		this.stage = null;
		this.obsList = FXCollections.observableArrayList();
	}

	// 이벤트등록->핸들러함수연결, 이벤트등록및처리, UI객체초기화셋팅
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// 테이블뷰UI객체 컬럼초기화셋팅(컬럼을 12개를 만들고 ->Student 클래스필드와연결)
		tableViewColumnInitialize();
		// 학년레벨을 입력하는 초기화 처리
		comboBoxLevelInitialize();
		// 성별라디오버튼 그룹 초기화처리
		radioButtonGenderInitialize();
		// 점수입력창에 3자리수까지 입력셋팅(0~100점수만 입려요망)
		textFieldNumberFormat();
		// 데이타베이스(studentDB) 테이블(gradeTBL) 모든내용을 가져오기
		totalLoadList();
		// 총점버튼이벤트등록 핸들러함수처리
		btnTotal.setOnAction(e -> handleBtnTotalAction(e));
		// 평균버튼이벤트등록 핸들러함수처리
		btnAvg.setOnAction(e -> handleBtnAvgAction(e));
		// 초기화버튼이벤트등록 핸들러함수처리
		btnInit.setOnAction(e -> handleBtnInitAction(e));
		// 등록버튼 이벤트등록 핸들러함수처리
		btnOk.setOnAction(e -> handleBtnOkAction(e));
		// 찾기버튼 이벤트등록 핸들러함수처리
		btnSearch.setOnAction(e -> handleBtnSearchAction(e));
		// 삭제버튼 이벤트등록 핸들러함수처리
		btnDelete.setOnAction(e -> handleBtnDeleteAction(e));
		// 수정버튼 이벤트등록 핸들러함수처리
		btnEdit.setOnAction(e -> handleBtnEditAction(e));
		// 리스트버튼 이벤트등록 핸들러함수처리
		btnList.setOnAction(e -> handleBtnListAction(e));
		// 테이블뷰를 선택을 했을때 이벤트등록 핸들러함수처리
		tableView.setOnMousePressed(e -> handleTableViewPressedAction(e));
		// 바차트버튼 이벤트등록 핸들러함수처리
		btnBarChart.setOnAction(e -> handleBtnBarChartAction(e));
		// 파이챠트 이벤트등록 핸들러 함수처리(테이블뷰 두번클릭하면  이벤트발생)
		tableView.setOnMouseClicked(e->handlePieChartAction(e));
		// 종료버튼이벤트등록및처리
		btnExit.setOnAction(e -> stage.close());
		// 기본적인 입력데이터 처리함수
		insertBasicData();

	}

	
	

	// *********************************************************
	// *********************************************************
	// *********************************************************
	// 리스트버튼 이벤트등록 핸들러함수처리
	private void handleBtnListAction(ActionEvent e) {
		obsList.clear();
		totalLoadList();
	}
	// 데이타베이스(studentDB) 테이블(gradeTBL) 모든내용을 가져오기
	private void totalLoadList()  {
		Connection con=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		try {
			//1. mysql 드라이버 로더데이타베이스 객체를 가져와야 된다. 
			Class.forName("com.mysql.jdbc.Driver");
			//2. 아이디와 패스워드를 데이타베이스에 접속요청을 허가받아서 커넥션객체를 얻어온다.
			con = DriverManager.getConnection("jdbc:mysql://localhost/studentdb","root","123456");
			if(con != null) {
				System.out.println("RootControoler.totalLoadList :DB 연결성공");
			}else {
				System.out.println("RootControoler.totalLoadList :DB 연결실패");
			}
			//3. con 객체를 가지고 쿼리문을 실행할수있다. (select, insert, update, delete)
			String query = "select * from gradeTBL";
			//4. 쿼리문을 실행하기위한 준비
			pstmt=con.prepareStatement(query);
			//5. 쿼리문을 실행한다.(결과값의 레코드내용을 배열로 가져온다. ) 
			rs=pstmt.executeQuery();
			//6. ResultSet 값을 한개씩 가져와서 ArrayList에 저장한다. 
			ArrayList<Student> arrayList = new ArrayList<Student>();
			while(rs.next()) {
				Student student = new Student(rs.getInt(1),
						rs.getString(2),
						rs.getString(3),
						rs.getString(4),
						rs.getString(5),
						rs.getString(6),
						rs.getString(7),
						rs.getString(8),
						rs.getString(9),
						rs.getString(10),
						rs.getString(11),
						rs.getString(12),
						rs.getString(13));
				arrayList.add(student);
			}//end of while
			//7. ArrayList에 있는값을 ObservableList<Student> obsList 입력한다. 
			for(int i=0; i < arrayList.size() ; i++ ) {
				Student s = arrayList.get(i);
				obsList.add(s);
			}
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("TotalList 점검요망");
			alert.setHeaderText("ToatalList 문제발생!");
			alert.setContentText("문제사항: " +e.getMessage());
			alert.showAndWait();
		} finally {
				try {
					if(rs!=null)	rs.close();
					if(pstmt!=null)	pstmt.close();
					if(con!=null)	con.close();
				} catch (SQLException e) {
					System.out.println("RootController.totalLoadList: "+e.getMessage());
				}
		}
	}
	// 파이챠트 이벤트등록 핸들러 함수처리(테이블뷰 두번클릭하면  이벤트발생)
	private void handlePieChartAction(MouseEvent e) {
		//이벤트에서 두번을 클릭을 했는지 체크한다. 
		if(e.getClickCount() != 2)  return;
		
		//스테이지(스타일,모달,사이즈고정,주스테이지) -> 신 ->화면내용
		try {
			Parent root=FXMLLoader.load(getClass().getResource("/view/piechart.fxml"));
			Scene scene=new Scene(root);
			Stage pieChartStage=new Stage(StageStyle.UTILITY);
			
			//이벤트등록 처리
			PieChart pieChart=(PieChart) scene.lookup("#pieChart");
			Button btnClose=(Button) scene.lookup("#btnClose");
			
			//두번클릭된 Student 객체를 가져오기
			Student student=obsList.get(tableViewSelectedIndex);
			
			//파이챠트에 입력할내용을 observable list 입력한다. 
			ObservableList  pieObsList=FXCollections.observableArrayList();
			pieObsList.add(new PieChart.Data("국어",Integer.parseInt(student.getKorean())));
			pieObsList.add(new PieChart.Data("영어",Integer.parseInt(student.getEnglish())));
			pieObsList.add(new PieChart.Data("수학",Integer.parseInt(student.getMath())));
			pieObsList.add(new PieChart.Data("과학",Integer.parseInt(student.getSic())));
			pieObsList.add(new PieChart.Data("사회",Integer.parseInt(student.getSoc())));
			pieObsList.add(new PieChart.Data("음악",Integer.parseInt(student.getMusic())));
			
			pieChart.setData(pieObsList);
			
			
			pieChartStage.initModality(Modality.WINDOW_MODAL);
			pieChartStage.initOwner(stage);
			pieChartStage.setScene(scene);
			pieChartStage.setTitle("개인성적챠트보고서");
			pieChartStage.show();
		
		} catch (IOException e1) {
			
		}
		
		
	}
	// 기본적인 입력데이터 처리함수
	private void insertBasicData() {
		txtName.setText("aaaa");
		cmbLevel.getSelectionModel().select(1);
		txtBan.setText("2");
		txtKo.setText("90");
		txtMath.setText("90");
		txtEng.setText("90");
		txtMusic.setText("80");
		txtSic.setText("90");
		txtSoc.setText("80");
	}
	
	private void handleBtnBarChartAction(ActionEvent e) {
		//내용 -> 신 -> 스테이지(스타일,모달,주인스테이지, 사이즈크기변경) -> 보여준다. 
		try {
			if(obsList.size() == 0) throw new Exception();
			
			Parent root=FXMLLoader.load(getClass().getResource("/view/barchart.fxml"));
			Scene scene=new Scene(root);
			Stage barStage=new Stage(StageStyle.UTILITY);
			
			//이벤트등록 핸들러처리
			BarChart  barChart = (BarChart) scene.lookup("#barChart");
			Button  btnClose = (Button) scene.lookup("#btnClose");
			
			//1. XYChart 시리즈를 만든다. (국어)
			XYChart.Series seriesKorean= new  XYChart.Series();	
			seriesKorean.setName("국어");
			ObservableList koreanList=FXCollections.observableArrayList();
			for(int i=0; i < obsList.size()  ; i++) {
				Student student=obsList.get(i);
				koreanList.add(new XYChart.Data(student.getName(),
						Integer.parseInt(student.getKorean())));
			}
			seriesKorean.setData(koreanList);
			barChart.getData().add(seriesKorean);
			
			//2. XYChart 시리즈를 만든다. (영어)
			XYChart.Series seriesEnglish= new  XYChart.Series();	
			seriesEnglish.setName("영어");
			ObservableList englishList=FXCollections.observableArrayList();
			for(int i=0; i < obsList.size()  ; i++) {
				Student student=obsList.get(i);
				englishList.add(new XYChart.Data(student.getName(),
						Integer.parseInt(student.getEnglish())));
			}
			seriesEnglish.setData(englishList);
			barChart.getData().add(seriesEnglish);
			
			
			//3. XYChart 시리즈를 만든다. (수학)
			XYChart.Series seriesMath= new  XYChart.Series();	
			seriesMath.setName("수학");
			ObservableList mathList=FXCollections.observableArrayList();
			for(int i=0; i < obsList.size()  ; i++) {
				Student student=obsList.get(i);
				mathList.add(new XYChart.Data(student.getName(),
						Integer.parseInt(student.getMath())));
			}
			seriesMath.setData(mathList);
			barChart.getData().add(seriesMath);
			
			
			//4. XYChart 시리즈를 만든다. (과학)
			XYChart.Series seriesScience= new  XYChart.Series();	
			seriesScience.setName("과학");
			ObservableList scienceList=FXCollections.observableArrayList();
			for(int i=0; i < obsList.size()  ; i++) {
				Student student=obsList.get(i);
				scienceList.add(new XYChart.Data(student.getName(),
						Integer.parseInt(student.getSic())));
			}
			seriesScience.setData(scienceList);
			barChart.getData().add(seriesScience);
			
			//5. XYChart 시리즈를 만든다. (사회)
			XYChart.Series seriesSociety= new  XYChart.Series();	
			seriesSociety.setName("사회");
			ObservableList societyList=FXCollections.observableArrayList();
			for(int i=0; i < obsList.size()  ; i++) {
				Student student=obsList.get(i);
				societyList.add(new XYChart.Data(student.getName(),
						Integer.parseInt(student.getSoc())));
			}
			seriesSociety.setData(societyList);
			barChart.getData().add(seriesSociety);
			
			//6. XYChart 시리즈를 만든다. (음악)
			XYChart.Series seriesMusic= new  XYChart.Series();	
			seriesMusic.setName("음악");
			ObservableList musicList=FXCollections.observableArrayList();
			for(int i=0; i < obsList.size()  ; i++) {
				Student student=obsList.get(i);
				musicList.add(new XYChart.Data(student.getName(),
						Integer.parseInt(student.getMusic())));
			}
			seriesMusic.setData(musicList);
			barChart.getData().add(seriesMusic);
			
			//닫기버튼 이벤트 등록 처리
			btnClose.setOnAction((event)->{barStage.close();});
			
			barStage.initModality(Modality.WINDOW_MODAL);
			barStage.setResizable(false);
			barStage.initOwner(stage);
			barStage.setScene(scene);
			barStage.setTitle("성적 막대그래프");
			barStage.show();
		} catch (Exception e1 ) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("테이블뷰 리스트 입력요망");
			alert.setHeaderText("데이타 리스트를 입력하시오!");
			alert.setContentText("다음에는 주의하세요. ");
			alert.showAndWait();
		}
	}
	// 점수입력창에 3자리수까지 입력셋팅(0~100점수만 입려요망)
	private void textFieldNumberFormat() {
		//10진수 3자리까지만 입력틀을 제공하는 객체
		
		
			DecimalFormat decimalFormat=new DecimalFormat("###");
			
			txtKo.setTextFormatter(new TextFormatter<>( e ->{
				//1. 글자입력이 스페이스공백이면 다시 이벤트를 돌려준다. 
				if(e.getControlNewText().isEmpty()) { return e;}
				//2. 위치조사(키보드치는 위치추적해간다.)
				ParsePosition parsePosition=new ParsePosition(0);
				//숫자만 받겠다.(3글자만)
				Object object=decimalFormat.parse(e.getControlNewText(),parsePosition );
				int number =Integer.MAX_VALUE;
				try {
					number = Integer.parseInt(e.getControlNewText());
				}catch(NumberFormatException e2) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("점수입력요망");
					alert.setHeaderText("점수(0~100)를 입력하시오!");
					alert.setContentText("숫자외의 다른문자입력되지않는다.  ");
					alert.showAndWait();
				}
				if(object == null || e.getControlNewText().length() >=4 || number > 100 ) {
					return null;
				}else {
					return e;
				}
			}));
			
			txtEng.setTextFormatter(new TextFormatter<>( e ->{
				//1. 글자입력이 스페이스공백이면 다시 이벤트를 돌려준다. 
				if(e.getControlNewText().isEmpty()) { return e;}
				//2. 위치조사(키보드치는 위치추적해간다.)
				ParsePosition parsePosition=new ParsePosition(0);
				//숫자만 받겠다.(3글자만)
				Object object=decimalFormat.parse(e.getControlNewText(),parsePosition );
				int number =Integer.MAX_VALUE;
				try {
					number = Integer.parseInt(e.getControlNewText());
				}catch(NumberFormatException e2) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("점수입력요망");
					alert.setHeaderText("점수(0~100)를 입력하시오!");
					alert.setContentText("숫자외의 다른문자입력되지않는다.  ");
					alert.showAndWait();
				}
				if(object == null || e.getControlNewText().length() >=4 || number > 100 ) {
					return null;
				}else {
					return e;
				}
			}));
			
			txtMath.setTextFormatter(new TextFormatter<>( e ->{
				//1. 글자입력이 스페이스공백이면 다시 이벤트를 돌려준다. 
				if(e.getControlNewText().isEmpty()) { return e;}
				//2. 위치조사(키보드치는 위치추적해간다.)
				ParsePosition parsePosition=new ParsePosition(0);
				//숫자만 받겠다.(3글자만)
				Object object=decimalFormat.parse(e.getControlNewText(),parsePosition );
				int number =Integer.MAX_VALUE;
				try {
					number = Integer.parseInt(e.getControlNewText());
				}catch(NumberFormatException e2) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("점수입력요망");
					alert.setHeaderText("점수(0~100)를 입력하시오!");
					alert.setContentText("숫자외의 다른문자입력되지않는다.  ");
					alert.showAndWait();
				}
				if(object == null || e.getControlNewText().length() >=4 || number > 100 ) {
					return null;
				}else {
					return e;
				}
			}));
			
			txtMusic.setTextFormatter(new TextFormatter<>( e ->{
				//1. 글자입력이 스페이스공백이면 다시 이벤트를 돌려준다. 
				if(e.getControlNewText().isEmpty()) { return e;}
				//2. 위치조사(키보드치는 위치추적해간다.)
				ParsePosition parsePosition=new ParsePosition(0);
				//숫자만 받겠다.(3글자만)
				Object object=decimalFormat.parse(e.getControlNewText(),parsePosition );
				int number =Integer.MAX_VALUE;
				try {
					number = Integer.parseInt(e.getControlNewText());
				}catch(NumberFormatException e2) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("점수입력요망");
					alert.setHeaderText("점수(0~100)를 입력하시오!");
					alert.setContentText("숫자외의 다른문자입력되지않는다.  ");
					alert.showAndWait();
				}
				if(object == null || e.getControlNewText().length() >=4 || number > 100 ) {
					return null;
				}else {
					return e;
				}
			}));
			
			txtSic.setTextFormatter(new TextFormatter<>( e ->{
				//1. 글자입력이 스페이스공백이면 다시 이벤트를 돌려준다. 
				if(e.getControlNewText().isEmpty()) { return e;}
				//2. 위치조사(키보드치는 위치추적해간다.)
				ParsePosition parsePosition=new ParsePosition(0);
				//숫자만 받겠다.(3글자만)
				Object object=decimalFormat.parse(e.getControlNewText(),parsePosition );
				int number =Integer.MAX_VALUE;
				try {
					number = Integer.parseInt(e.getControlNewText());
				}catch(NumberFormatException e2) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("점수입력요망");
					alert.setHeaderText("점수(0~100)를 입력하시오!");
					alert.setContentText("숫자외의 다른문자입력되지않는다.  ");
					alert.showAndWait();
				}
				if(object == null || e.getControlNewText().length() >=4 || number > 100 ) {
					return null;
				}else {
					return e;
				}
			}));
			
			txtSoc.setTextFormatter(new TextFormatter<>( e ->{
				//1. 글자입력이 스페이스공백이면 다시 이벤트를 돌려준다. 
				if(e.getControlNewText().isEmpty()) { return e;}
				//2. 위치조사(키보드치는 위치추적해간다.)
				ParsePosition parsePosition=new ParsePosition(0);
				//숫자만 받겠다.(3글자만)
				Object object=decimalFormat.parse(e.getControlNewText(),parsePosition );
				int number =Integer.MAX_VALUE;
				try {
					number = Integer.parseInt(e.getControlNewText());
				}catch(NumberFormatException e2) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("점수입력요망");
					alert.setHeaderText("점수(0~100)를 입력하시오!");
					alert.setContentText("숫자외의 다른문자입력되지않는다.  ");
					alert.showAndWait();
				}
				if(object == null || e.getControlNewText().length() >=4 || number > 100 ) {
					return null;
				}else {
					return e;
				}
			}));
			
	}
	// 수정버튼 이벤트등록 핸들러함수처리
	private void handleBtnEditAction(ActionEvent event) {
		// formedit.fxml 화면을 로드해야된다.
		try {
			// 화면내용->씬 -> 스테이지(주인스테이지)-> 보여주면된다. 을 가져왔어요.
			Parent root = FXMLLoader.load(getClass().getResource("/view/formedit.fxml"));
			
			// scene(화면내용) 만든다.
			Scene scene = new Scene(root);
			
			Stage editStage=new Stage(StageStyle.UTILITY);
			
			// ++++++++이벤트등록 및 핸들러처리+++++++++++
			// @FXML private TextField txtName -> 이것을 할수없다. (컨틀롤러가 없기때문)
			TextField txtNo = (TextField) scene.lookup("#txtNo");
			TextField txtName = (TextField) scene.lookup("#txtName");
			TextField txtYear = (TextField) scene.lookup("#txtYear");
			TextField txtBan = (TextField) scene.lookup("#txtBan");
			TextField txtGender = (TextField) scene.lookup("#txtGender");
			TextField txtKorean = (TextField) scene.lookup("#txtKorean");
			TextField txtEnglish = (TextField) scene.lookup("#txtEnglish");
			TextField txtMath = (TextField) scene.lookup("#txtMath");
			TextField txtSic = (TextField) scene.lookup("#txtSic");
			TextField txtSoc = (TextField) scene.lookup("#txtSoc");
			TextField txtMusic = (TextField) scene.lookup("#txtMusic");
			TextField txtTotal = (TextField) scene.lookup("#txtTotal");
			TextField txtAvg = (TextField) scene.lookup("#txtAvg");
			Button btnCal = (Button) scene.lookup("#btnCal");
			Button btnFormAdd = (Button) scene.lookup("#btnFormAdd");
			Button btnFormCancel = (Button) scene.lookup("#btnFormCancel");

			// 테이블뷰에서 선택된 위치값을가지고 observable list에서 그 위치를 찾아서
			// 해당된 Student 객체를 가져오면 된다.
			Student student = obsList.get(tableViewSelectedIndex);
			txtNo.setText(String.valueOf(student.getNo()));
			txtName.setText(student.getName());
			txtYear.setText(student.getLevel());
			txtBan.setText(student.getBan());
			txtGender.setText(student.getGender());
			txtKorean.setText(student.getKorean());
			txtEnglish.setText(student.getEnglish());
			txtMath.setText(student.getMath());
			txtSic.setText(student.getSic());
			txtSoc.setText(student.getSoc());
			txtMusic.setText(student.getMusic());
			txtTotal.setText(student.getTotal());
			txtAvg.setText(student.getAvg());
			
			//txtNo 텍스트필드를 read only(읽기만 가능) 만든다.(번호,이름,학년,반,성별)
			txtNo.setDisable(true);
			txtName.setDisable(true);
			txtYear.setDisable(true);
			txtBan.setDisable(true);
			txtGender.setDisable(true);
			
			
			//계산기능에 해당된 이벤트등록 및 핸들러 처리기능
			btnCal.setOnAction(e-> {
				int korean = Integer.parseInt(txtKorean.getText());
				int english = Integer.parseInt(txtEnglish.getText());
				int math = Integer.parseInt(txtMath.getText());
				int sic = Integer.parseInt(txtSic.getText());
				int soc = Integer.parseInt(txtSoc.getText());
				int music = Integer.parseInt(txtMusic.getText());
				int total = korean + english + math + sic + soc + music;
				double avg = total / 6.0;
				txtTotal.setText(String.valueOf(total));
				txtAvg.setText(String.format("%.1f", avg));
			});
			
			//저장버튼 이벤트등록 및 핸들러 처리기능
			btnFormAdd.setOnAction(e-> {
				Student stu = obsList.get(tableViewSelectedIndex);
				stu.setKorean(txtKorean.getText());
				stu.setEnglish(txtEnglish.getText());
				stu.setMath(txtMath.getText());
				stu.setSic(txtSic.getText());
				stu.setSoc(txtSoc.getText());
				stu.setMusic(txtMusic.getText());
				stu.setTotal(txtTotal.getText());
				stu.setAvg(txtAvg.getText());
				
				//데이타베이스 작업을 진행한다. 
				//1. 데이타베이스해야되는 내용
				Connection con=null;
				PreparedStatement pstmt=null;
				try {
					//1. mysql 드라이버 로더데이타베이스 객체를 가져와야 된다. 
					Class.forName("com.mysql.jdbc.Driver");
					//2. 아이디와 패스워드를 데이타베이스에 접속요청을 허가받아서 커넥션객체를 얻어온다.
					con = DriverManager.getConnection("jdbc:mysql://localhost/studentdb","root","123456");
					if(con != null) {
						System.out.println("RootControoler.BtnDeleteAction :DB 연결성공");
					}else {
						System.out.println("RootControoler.BtnDeleteAction :DB 연결실패");
					}
					//3. con 객체를 가지고 쿼리문을 실행할수있다. (select, insert, update, delete)
					String query = "update gradeTBL set korean=?, english=?, math=?, sic=?, "
							+ "soc=?, music=?, total=?, average=? where no=?";
					//4. 쿼리문을 실행하기위한 준비
					pstmt=con.prepareStatement(query);
					//5. 어떤레코드를 지워야할지 쿼리문에 ? no 번호를 연결한다. 
					pstmt.setString(1,stu.getKorean());
					pstmt.setString(2,stu.getEnglish());
					pstmt.setString(3,stu.getMath());
					pstmt.setString(4,stu.getSic());
					pstmt.setString(5,stu.getSoc());
					pstmt.setString(6,stu.getMusic());
					pstmt.setString(7,stu.getTotal());
					pstmt.setString(8,stu.getAvg());
					pstmt.setInt(9,stu.getNo());
					
					//5. 쿼리문을 실행한다.
					//(쿼리뭄을 실행해서 레코드내용 결과값을 RecordSet로 가져온다. executeQuery();)
					//(단지 쿼리문만 실행만한다. : executeUpdate();) 
					int returnValue=pstmt.executeUpdate();
					
					if(returnValue != 0) {
						//테이블뷰 obsList 해당된 위치에 수정된 객체값을 입력한다. 
						obsList.set(tableViewSelectedIndex, stu);
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("수정 성공");
						alert.setHeaderText(student.getNo() +"번 수정 성공");
						alert.setContentText(student.getName()+"님 수정했습니다." );
						alert.showAndWait();
					}else {
						throw new Exception("삭제에 문제있음");
					}

				} catch (Exception e1) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("수정 점검요망");
					alert.setHeaderText("수정 문제발생! \n RootController.BtnEditAction");
					alert.setContentText("문제사항: " +e1.getMessage());
					alert.showAndWait();
				} finally {
						try {
							if(pstmt!=null)	pstmt.close();
							if(con!=null)	con.close();
						} catch (SQLException e2) {
							System.out.println("RootController.BtnEditAction: "+e2.getMessage());
						}
				}
			});
			
			//취소버튼을 누르면 창을 닫는다. 
			btnFormCancel.setOnAction(e-> { editStage.close();	});
			
			// 스테이지(주인스테이지)를 만든다.(*모달, 모달리스), 스테이지(씬), 스테이지크기조절방어
			// editStage = new Stage(StageStyle.UTILITY);
			editStage.initModality(Modality.WINDOW_MODAL);
			editStage.initOwner(stage);
			editStage.setScene(scene);
			editStage.setTitle("성적프로그램 수정창");
			editStage.setResizable(false);
			editStage.show();
		} catch (IndexOutOfBoundsException | IOException e1) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("수정창 에러");
			alert.setHeaderText("점검요망!");
			alert.setContentText("정신차리세요! ");
			alert.showAndWait();
		}

	}

	// 테이블뷰를 선택을 했을때 이벤트등록 핸들러함수처리
	private void handleTableViewPressedAction(MouseEvent e) {
		tableViewSelectedIndex = tableView.getSelectionModel().getSelectedIndex();
	}

	// 삭제버튼 이벤트등록 핸들러함수처리
	private void handleBtnDeleteAction(ActionEvent event) {
		//1. 데이타베이스해야되는 내용
		Connection con=null;
		PreparedStatement pstmt=null;
		try {
			//1. mysql 드라이버 로더데이타베이스 객체를 가져와야 된다. 
			Class.forName("com.mysql.jdbc.Driver");
			//2. 아이디와 패스워드를 데이타베이스에 접속요청을 허가받아서 커넥션객체를 얻어온다.
			con = DriverManager.getConnection("jdbc:mysql://localhost/studentdb","root","123456");
			if(con != null) {
				System.out.println("RootControoler.BtnDeleteAction :DB 연결성공");
			}else {
				System.out.println("RootControoler.BtnDeleteAction :DB 연결실패");
			}
			//3. con 객체를 가지고 쿼리문을 실행할수있다. (select, insert, update, delete)
			String query = "delete from gradeTBL where no = ? ";
			//4. 쿼리문을 실행하기위한 준비
			pstmt=con.prepareStatement(query);
			//5. 어떤레코드를 지워야할지 쿼리문에 ? no 번호를 연결한다. 
			Student student=obsList.get(tableViewSelectedIndex);
			int no = student.getNo();
			pstmt.setInt(1, no);
			//5. 쿼리문을 실행한다.
			//(쿼리뭄을 실행해서 레코드내용 결과값을 RecordSet로 가져온다. executeQuery();)
			//(단지 쿼리문만 실행만한다. : executeUpdate();) 
			int returnValue=pstmt.executeUpdate();
			
			if(returnValue != 0) {
				obsList.remove(tableViewSelectedIndex);
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("삭제 성공");
				alert.setHeaderText(student.getNo() +"번 삭제 성공");
				alert.setContentText(student.getName()+"님 안녕" );
				alert.showAndWait();
			}else {
				throw new Exception("삭제에 문제있음");
			}

		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("삭제 점검요망");
			alert.setHeaderText("삭제 문제발생! \n RootController.BtnDeleteAction");
			alert.setContentText("문제사항: " +e.getMessage());
			alert.showAndWait();
		} finally {
				try {
					if(pstmt!=null)	pstmt.close();
					if(con!=null)	con.close();
				} catch (SQLException e) {
					System.out.println("RootController.BtnDeleteAction: "+e.getMessage());
				}
		}

	}

	// 찾기버튼 이벤트등록 핸들러함수처리
	private void handleBtnSearchAction(ActionEvent e) {
		// 테이블뷰에 들어있는 데이타제공 obsList 내용을 하나씩 가져온다.
		try {
			if (txtSearch.getText().trim().equals(""))
				throw new Exception();
			//obsList에서 검색한것인데.. 원래는 테이블에서(gradeTBL) 찿아서 가져온다. 
//			for (Student student : obsList) {
//				if (student.getName().equals(txtSearch.getText())) {
//					tableView.getSelectionModel().select(student);
//				}
//			}
			//데이타베이스 시작점를 실행합니다. 
			Connection con=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			try {
				//1. mysql 드라이버 로더데이타베이스 객체를 가져와야 된다. 
				Class.forName("com.mysql.jdbc.Driver");
				//2. 아이디와 패스워드를 데이타베이스에 접속요청을 허가받아서 커넥션객체를 얻어온다.
				con = DriverManager.getConnection("jdbc:mysql://localhost/studentdb","root","123456");
				if(con != null) {
					System.out.println("RootControoler.totalLoadList :DB 연결성공");
				}else {
					System.out.println("RootControoler.totalLoadList :DB 연결실패");
				}
				//3. con 객체를 가지고 쿼리문을 실행할수있다. (select, insert, update, delete)
				String query = "select * from gradeTBL where name = ?";
				//4. 쿼리문을 실행하기위한 준비
				pstmt=con.prepareStatement(query);
				pstmt.setString(1,txtSearch.getText().trim());
				
				//5. 쿼리문을 실행한다.(결과값의 레코드내용을 배열로 가져온다. ) 
				rs=pstmt.executeQuery();
				//6. ResultSet 값을 한개씩 가져와서 ArrayList에 저장한다. 
				ArrayList<Student> arrayList = new ArrayList<Student>();
				while(rs.next()) {
					Student student = new Student(rs.getInt(1),
							rs.getString(2),
							rs.getString(3),
							rs.getString(4),
							rs.getString(5),
							rs.getString(6),
							rs.getString(7),
							rs.getString(8),
							rs.getString(9),
							rs.getString(10),
							rs.getString(11),
							rs.getString(12),
							rs.getString(13));
					arrayList.add(student);
				}//end of while
				//7. ArrayList에 있는값을 ObservableList<Student> obsList 입력한다. 
				if(arrayList.size() != 0 ) {
					obsList.clear();
					for(int i=0; i < arrayList.size() ; i++ ) {
						Student s = arrayList.get(i);
						obsList.add(s);
					}
				}
				
			} catch (Exception e1) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("검색 점검요망");
				alert.setHeaderText("검색 문제발생!");
				alert.setContentText("문제사항: " +e1.getMessage());
				alert.showAndWait();
			} finally {
					try {
						if(rs!=null)	rs.close();
						if(pstmt!=null)	pstmt.close();
						if(con!=null)	con.close();
					} catch (SQLException e2) {
						System.out.println("RootController.BtnSearchAction: "+e2.getMessage());
					}
			}
			//데이타베이스 종점===============
			
		} catch (Exception event) {
			// 경고창이 만들어짐. 스테이지, 씬, 화면내용(고정화시킴)
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("이름입력요망");
			alert.setHeaderText("이름를 입력하시오!");
			alert.setContentText("다음에는 주의하세요. ");
			alert.showAndWait();
		}
	}

	// 성별라디오버튼 그룹 초기화처리
	private void radioButtonGenderInitialize() {
		group = new ToggleGroup();
		rdoMale.setToggleGroup(group);
		rdoFemale.setToggleGroup(group);
		rdoMale.setSelected(true);
	}

	// 학년레벨을 입력하는 초기화 처리(1~6학년)
	private void comboBoxLevelInitialize() {
		ObservableList<String> obsList = FXCollections.observableArrayList();
		obsList.addAll("1", "2", "3", "4", "5", "6");
		cmbLevel.setItems(obsList);
	}

	// 테이블뷰에 등록버튼이벤트등록 핸들러함수처리
	private void handleBtnOkAction(ActionEvent event) {
		//1. 데이타베이스해야되는 내용
		Connection con=null;
		PreparedStatement pstmt=null;
		try {
			//1. mysql 드라이버 로더데이타베이스 객체를 가져와야 된다. 
			Class.forName("com.mysql.jdbc.Driver");
			//2. 아이디와 패스워드를 데이타베이스에 접속요청을 허가받아서 커넥션객체를 얻어온다.
			con = DriverManager.getConnection("jdbc:mysql://localhost/studentdb","root","123456");
			if(con != null) {
				System.out.println("RootControoler.BtnDeleteAction :DB 연결성공");
			}else {
				System.out.println("RootControoler.BtnDeleteAction :DB 연결실패");
			}
			//3. con 객체를 가지고 쿼리문을 실행할수있다. (select, insert, update, delete)
			String query = "insert into gradeTBL values(null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
			//4. 쿼리문을 실행하기위한 준비
			pstmt=con.prepareStatement(query);
			//5. 어떤레코드를 지워야할지 쿼리문에 ? no 번호를 연결한다. 
			Student s=new Student(txtName.getText(), cmbLevel.getSelectionModel().getSelectedItem().toString(),
					txtBan.getText(), ((RadioButton) group.getSelectedToggle()).getText(), txtKo.getText(),
					txtEng.getText(), txtMath.getText(), txtSic.getText(), txtSoc.getText(), txtMusic.getText(),
					txtTotal.getText(), txtAvg.getText());
			pstmt.setString(1,s.getName());
			pstmt.setString(2,s.getLevel());
			pstmt.setString(3,s.getBan());
			pstmt.setString(4,s.getGender());
			pstmt.setString(5,s.getKorean());
			pstmt.setString(6,s.getEnglish());
			pstmt.setString(7,s.getMath());
			pstmt.setString(8,s.getSic());
			pstmt.setString(9,s.getSoc());
			pstmt.setString(10,s.getMusic());
			pstmt.setString(11,s.getTotal());
			pstmt.setString(12,s.getAvg());
			//5. 쿼리문을 실행한다.
			//(쿼리뭄을 실행해서 레코드내용 결과값을 RecordSet로 가져온다. executeQuery();)
			//(단지 쿼리문만 실행만한다. : executeUpdate();) 
			int returnValue=pstmt.executeUpdate();
			
			if(returnValue != 0) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("삽입 성공");
				alert.setHeaderText(s.getName() +"님 삽입 성공");
				alert.setContentText(s.getName()+"님 방가방가" );
				alert.showAndWait();
				
				obsList.clear();
				totalLoadList();
				
			}else {
				throw new Exception(s.getName()+"님 삽입에 문제있음");
			}

		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("삽입 점검요망");
			alert.setHeaderText("삽입 문제발생! \n RootController.BtnOkAction");
			alert.setContentText("문제사항: " +e.getMessage());
			alert.showAndWait();
		} finally {
				try {
					if(pstmt!=null)	pstmt.close();
					if(con!=null)	con.close();
				} catch (SQLException e) {
					System.out.println("RootController.BtnOkAction: "+e.getMessage());
				}
		}
		
		
//		try {
//			obsList.add(new Student(txtName.getText(), cmbLevel.getSelectionModel().getSelectedItem().toString(),
//					txtBan.getText(), ((RadioButton) group.getSelectedToggle()).getText(), txtKo.getText(),
//					txtEng.getText(), txtMath.getText(), txtSic.getText(), txtSoc.getText(), txtMusic.getText(),
//					txtTotal.getText(), txtAvg.getText()));
//		} catch (NullPointerException e) {
//			// 경고창이 만들어짐. 스테이지, 씬, 화면내용(고정화시킴)
//			Alert alert = new Alert(AlertType.ERROR);
//			alert.setTitle("등록입력문제발생");
//			alert.setHeaderText("Student 객체 점검을 해주세요.!");
//			alert.setContentText("다음에는 주의하세요. ");
//			alert.showAndWait();
//		}
	}

	// 초기화버튼이벤트등록 핸들러함수처리
	private void handleBtnInitAction(ActionEvent e) {
		txtName.clear();
		cmbLevel.getSelectionModel().clearSelection();
		rdoMale.setSelected(true);
		txtBan.clear();
		txtKo.clear();
		txtEng.clear();
		txtMath.clear();
		txtSic.clear();
		txtSoc.clear();
		txtMusic.clear();
		txtTotal.clear();
		txtAvg.clear();
	}

	// 평균버튼이벤트등록 핸들러함수처리
	private void handleBtnAvgAction(ActionEvent e) {
		try {
			double avg = Integer.parseInt(txtTotal.getText()) / 6.0;
			txtAvg.setText(String.format("%.1f", avg));
		} catch (NumberFormatException event) {
			// 경고창이 만들어짐. 스테이지, 씬, 화면내용(고정화시킴)
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("총점입력요망");
			alert.setHeaderText("총점이 입력이 안됐어요 !");
			alert.setContentText("다음에는 주의하세요. ");
			alert.showAndWait();
		}
	}

	// 총점버튼이벤트등록 핸들러함수처리(6개과목합산총점을구한다.)
	private void handleBtnTotalAction(ActionEvent e) {
		try {
			int korean = Integer.parseInt(txtKo.getText());
			int english = Integer.parseInt(txtEng.getText());
			int math = Integer.parseInt(txtMath.getText());
			int sic = Integer.parseInt(txtSic.getText());
			int soc = Integer.parseInt(txtSoc.getText());
			int music = Integer.parseInt(txtMusic.getText());
			int total = korean + english + math + sic + soc + music;
			txtTotal.setText(String.valueOf(total));
		} catch (NumberFormatException event) {
			// 경고창이 만들어짐. 스테이지, 씬, 화면내용(고정화시킴)
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("점수입력요망");
			alert.setHeaderText("점수를 입력하시오!");
			alert.setContentText("다음에는 주의하세요. ");
			alert.showAndWait();
		}
	}

	// 테이블뷰UI객체 컬럼초기화셋팅(컬럼을 12개를 만들고 ->Student 클래스필드와연결)
	private void tableViewColumnInitialize() {
		TableColumn colNo = new TableColumn("NO");
		colNo.setMaxWidth(40);
		colNo.setCellValueFactory(new PropertyValueFactory("no"));
		
		TableColumn colName = new TableColumn("성명");
		colName.setMaxWidth(60);
		colName.setCellValueFactory(new PropertyValueFactory("name"));

		TableColumn colLevel = new TableColumn("학년");
		colLevel.setMaxWidth(30);
		colLevel.setCellValueFactory(new PropertyValueFactory("level"));

		TableColumn colBan = new TableColumn("반");
		colBan.setMaxWidth(30);
		colBan.setCellValueFactory(new PropertyValueFactory<>("ban"));

		TableColumn colGender = new TableColumn("성별");
		colGender.setMaxWidth(40);
		colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));

		TableColumn colKorean = new TableColumn("국어");
		colKorean.setMaxWidth(40);
		colKorean.setCellValueFactory(new PropertyValueFactory<>("korean"));

		TableColumn colEnglish = new TableColumn("영어");
		colEnglish.setMaxWidth(40);
		colEnglish.setCellValueFactory(new PropertyValueFactory<>("english"));

		TableColumn colMath = new TableColumn("수학");
		colMath.setMaxWidth(40);
		colMath.setCellValueFactory(new PropertyValueFactory<>("math"));

		TableColumn colSic = new TableColumn("과학");
		colSic.setMaxWidth(40);
		colSic.setCellValueFactory(new PropertyValueFactory<>("sic"));

		TableColumn colSoc = new TableColumn("사회");
		colSoc.setMaxWidth(40);
		colSoc.setCellValueFactory(new PropertyValueFactory<>("soc"));

		TableColumn colMusic = new TableColumn("음악");
		colMusic.setMaxWidth(40);
		colMusic.setCellValueFactory(new PropertyValueFactory<>("music"));

		TableColumn colTotal = new TableColumn("총점");
		colTotal.setMaxWidth(40);
		colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

		TableColumn colAvg = new TableColumn("평균");
		colAvg.setMaxWidth(50);
		colAvg.setCellValueFactory(new PropertyValueFactory<>("avg"));

		tableView.getColumns().addAll(colNo, colName, colLevel, colBan, colGender, colKorean, colEnglish, colMath, colSic,
				colSoc, colMusic, colTotal, colAvg);
		tableView.setItems(obsList);

		DecimalFormat format = new DecimalFormat("###");
		// 점수 입력시 길이 제한 이벤트 처리
		txtKo.setTextFormatter(new TextFormatter<>(event -> {
			if (event.getControlNewText().isEmpty()) {
				return event;
			}
			ParsePosition parsePosition = new ParsePosition(0);
			Object object = format.parse(event.getControlNewText(), parsePosition);
			
			int num = Integer.parseInt(event.getControlNewText());
			
			if (object == null 	|| event.getControlNewText().length()>= 4 || num > 100) {
				return null;
			} else {
				return event;
			}
		}));

	}

}
