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

	// �̺�Ʈ���->�ڵ鷯�Լ�����, �̺�Ʈ��Ϲ�ó��, UI��ü�ʱ�ȭ����
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// ���̺��UI��ü �÷��ʱ�ȭ����(�÷��� 12���� ����� ->Student Ŭ�����ʵ�Ϳ���)
		tableViewColumnInitialize();
		// �гⷹ���� �Է��ϴ� �ʱ�ȭ ó��
		comboBoxLevelInitialize();
		// ����������ư �׷� �ʱ�ȭó��
		radioButtonGenderInitialize();
		// �����Է�â�� 3�ڸ������� �Է¼���(0~100������ �Է����)
		textFieldNumberFormat();
		// ����Ÿ���̽�(studentDB) ���̺�(gradeTBL) ��系���� ��������
		totalLoadList();
		// ������ư�̺�Ʈ��� �ڵ鷯�Լ�ó��
		btnTotal.setOnAction(e -> handleBtnTotalAction(e));
		// ��չ�ư�̺�Ʈ��� �ڵ鷯�Լ�ó��
		btnAvg.setOnAction(e -> handleBtnAvgAction(e));
		// �ʱ�ȭ��ư�̺�Ʈ��� �ڵ鷯�Լ�ó��
		btnInit.setOnAction(e -> handleBtnInitAction(e));
		// ��Ϲ�ư �̺�Ʈ��� �ڵ鷯�Լ�ó��
		btnOk.setOnAction(e -> handleBtnOkAction(e));
		// ã���ư �̺�Ʈ��� �ڵ鷯�Լ�ó��
		btnSearch.setOnAction(e -> handleBtnSearchAction(e));
		// ������ư �̺�Ʈ��� �ڵ鷯�Լ�ó��
		btnDelete.setOnAction(e -> handleBtnDeleteAction(e));
		// ������ư �̺�Ʈ��� �ڵ鷯�Լ�ó��
		btnEdit.setOnAction(e -> handleBtnEditAction(e));
		// ����Ʈ��ư �̺�Ʈ��� �ڵ鷯�Լ�ó��
		btnList.setOnAction(e -> handleBtnListAction(e));
		// ���̺�並 ������ ������ �̺�Ʈ��� �ڵ鷯�Լ�ó��
		tableView.setOnMousePressed(e -> handleTableViewPressedAction(e));
		// ����Ʈ��ư �̺�Ʈ��� �ڵ鷯�Լ�ó��
		btnBarChart.setOnAction(e -> handleBtnBarChartAction(e));
		// ����íƮ �̺�Ʈ��� �ڵ鷯 �Լ�ó��(���̺�� �ι�Ŭ���ϸ�  �̺�Ʈ�߻�)
		tableView.setOnMouseClicked(e->handlePieChartAction(e));
		// �����ư�̺�Ʈ��Ϲ�ó��
		btnExit.setOnAction(e -> stage.close());
		// �⺻���� �Էµ����� ó���Լ�
		insertBasicData();

	}

	
	

	// *********************************************************
	// *********************************************************
	// *********************************************************
	// ����Ʈ��ư �̺�Ʈ��� �ڵ鷯�Լ�ó��
	private void handleBtnListAction(ActionEvent e) {
		obsList.clear();
		totalLoadList();
	}
	// ����Ÿ���̽�(studentDB) ���̺�(gradeTBL) ��系���� ��������
	private void totalLoadList()  {
		Connection con=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		try {
			//1. mysql ����̹� �δ�����Ÿ���̽� ��ü�� �����;� �ȴ�. 
			Class.forName("com.mysql.jdbc.Driver");
			//2. ���̵�� �н����带 ����Ÿ���̽��� ���ӿ�û�� �㰡�޾Ƽ� Ŀ�ؼǰ�ü�� ���´�.
			con = DriverManager.getConnection("jdbc:mysql://localhost/studentdb","root","123456");
			if(con != null) {
				System.out.println("RootControoler.totalLoadList :DB ���Ἲ��");
			}else {
				System.out.println("RootControoler.totalLoadList :DB �������");
			}
			//3. con ��ü�� ������ �������� �����Ҽ��ִ�. (select, insert, update, delete)
			String query = "select * from gradeTBL";
			//4. �������� �����ϱ����� �غ�
			pstmt=con.prepareStatement(query);
			//5. �������� �����Ѵ�.(������� ���ڵ峻���� �迭�� �����´�. ) 
			rs=pstmt.executeQuery();
			//6. ResultSet ���� �Ѱ��� �����ͼ� ArrayList�� �����Ѵ�. 
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
			//7. ArrayList�� �ִ°��� ObservableList<Student> obsList �Է��Ѵ�. 
			for(int i=0; i < arrayList.size() ; i++ ) {
				Student s = arrayList.get(i);
				obsList.add(s);
			}
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("TotalList ���˿��");
			alert.setHeaderText("ToatalList �����߻�!");
			alert.setContentText("��������: " +e.getMessage());
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
	// ����íƮ �̺�Ʈ��� �ڵ鷯 �Լ�ó��(���̺�� �ι�Ŭ���ϸ�  �̺�Ʈ�߻�)
	private void handlePieChartAction(MouseEvent e) {
		//�̺�Ʈ���� �ι��� Ŭ���� �ߴ��� üũ�Ѵ�. 
		if(e.getClickCount() != 2)  return;
		
		//��������(��Ÿ��,���,���������,�ֽ�������) -> �� ->ȭ�鳻��
		try {
			Parent root=FXMLLoader.load(getClass().getResource("/view/piechart.fxml"));
			Scene scene=new Scene(root);
			Stage pieChartStage=new Stage(StageStyle.UTILITY);
			
			//�̺�Ʈ��� ó��
			PieChart pieChart=(PieChart) scene.lookup("#pieChart");
			Button btnClose=(Button) scene.lookup("#btnClose");
			
			//�ι�Ŭ���� Student ��ü�� ��������
			Student student=obsList.get(tableViewSelectedIndex);
			
			//����íƮ�� �Է��ҳ����� observable list �Է��Ѵ�. 
			ObservableList  pieObsList=FXCollections.observableArrayList();
			pieObsList.add(new PieChart.Data("����",Integer.parseInt(student.getKorean())));
			pieObsList.add(new PieChart.Data("����",Integer.parseInt(student.getEnglish())));
			pieObsList.add(new PieChart.Data("����",Integer.parseInt(student.getMath())));
			pieObsList.add(new PieChart.Data("����",Integer.parseInt(student.getSic())));
			pieObsList.add(new PieChart.Data("��ȸ",Integer.parseInt(student.getSoc())));
			pieObsList.add(new PieChart.Data("����",Integer.parseInt(student.getMusic())));
			
			pieChart.setData(pieObsList);
			
			
			pieChartStage.initModality(Modality.WINDOW_MODAL);
			pieChartStage.initOwner(stage);
			pieChartStage.setScene(scene);
			pieChartStage.setTitle("���μ���íƮ����");
			pieChartStage.show();
		
		} catch (IOException e1) {
			
		}
		
		
	}
	// �⺻���� �Էµ����� ó���Լ�
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
		//���� -> �� -> ��������(��Ÿ��,���,���ν�������, ������ũ�⺯��) -> �����ش�. 
		try {
			if(obsList.size() == 0) throw new Exception();
			
			Parent root=FXMLLoader.load(getClass().getResource("/view/barchart.fxml"));
			Scene scene=new Scene(root);
			Stage barStage=new Stage(StageStyle.UTILITY);
			
			//�̺�Ʈ��� �ڵ鷯ó��
			BarChart  barChart = (BarChart) scene.lookup("#barChart");
			Button  btnClose = (Button) scene.lookup("#btnClose");
			
			//1. XYChart �ø�� �����. (����)
			XYChart.Series seriesKorean= new  XYChart.Series();	
			seriesKorean.setName("����");
			ObservableList koreanList=FXCollections.observableArrayList();
			for(int i=0; i < obsList.size()  ; i++) {
				Student student=obsList.get(i);
				koreanList.add(new XYChart.Data(student.getName(),
						Integer.parseInt(student.getKorean())));
			}
			seriesKorean.setData(koreanList);
			barChart.getData().add(seriesKorean);
			
			//2. XYChart �ø�� �����. (����)
			XYChart.Series seriesEnglish= new  XYChart.Series();	
			seriesEnglish.setName("����");
			ObservableList englishList=FXCollections.observableArrayList();
			for(int i=0; i < obsList.size()  ; i++) {
				Student student=obsList.get(i);
				englishList.add(new XYChart.Data(student.getName(),
						Integer.parseInt(student.getEnglish())));
			}
			seriesEnglish.setData(englishList);
			barChart.getData().add(seriesEnglish);
			
			
			//3. XYChart �ø�� �����. (����)
			XYChart.Series seriesMath= new  XYChart.Series();	
			seriesMath.setName("����");
			ObservableList mathList=FXCollections.observableArrayList();
			for(int i=0; i < obsList.size()  ; i++) {
				Student student=obsList.get(i);
				mathList.add(new XYChart.Data(student.getName(),
						Integer.parseInt(student.getMath())));
			}
			seriesMath.setData(mathList);
			barChart.getData().add(seriesMath);
			
			
			//4. XYChart �ø�� �����. (����)
			XYChart.Series seriesScience= new  XYChart.Series();	
			seriesScience.setName("����");
			ObservableList scienceList=FXCollections.observableArrayList();
			for(int i=0; i < obsList.size()  ; i++) {
				Student student=obsList.get(i);
				scienceList.add(new XYChart.Data(student.getName(),
						Integer.parseInt(student.getSic())));
			}
			seriesScience.setData(scienceList);
			barChart.getData().add(seriesScience);
			
			//5. XYChart �ø�� �����. (��ȸ)
			XYChart.Series seriesSociety= new  XYChart.Series();	
			seriesSociety.setName("��ȸ");
			ObservableList societyList=FXCollections.observableArrayList();
			for(int i=0; i < obsList.size()  ; i++) {
				Student student=obsList.get(i);
				societyList.add(new XYChart.Data(student.getName(),
						Integer.parseInt(student.getSoc())));
			}
			seriesSociety.setData(societyList);
			barChart.getData().add(seriesSociety);
			
			//6. XYChart �ø�� �����. (����)
			XYChart.Series seriesMusic= new  XYChart.Series();	
			seriesMusic.setName("����");
			ObservableList musicList=FXCollections.observableArrayList();
			for(int i=0; i < obsList.size()  ; i++) {
				Student student=obsList.get(i);
				musicList.add(new XYChart.Data(student.getName(),
						Integer.parseInt(student.getMusic())));
			}
			seriesMusic.setData(musicList);
			barChart.getData().add(seriesMusic);
			
			//�ݱ��ư �̺�Ʈ ��� ó��
			btnClose.setOnAction((event)->{barStage.close();});
			
			barStage.initModality(Modality.WINDOW_MODAL);
			barStage.setResizable(false);
			barStage.initOwner(stage);
			barStage.setScene(scene);
			barStage.setTitle("���� ����׷���");
			barStage.show();
		} catch (Exception e1 ) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("���̺�� ����Ʈ �Է¿��");
			alert.setHeaderText("����Ÿ ����Ʈ�� �Է��Ͻÿ�!");
			alert.setContentText("�������� �����ϼ���. ");
			alert.showAndWait();
		}
	}
	// �����Է�â�� 3�ڸ������� �Է¼���(0~100������ �Է����)
	private void textFieldNumberFormat() {
		//10���� 3�ڸ������� �Է�Ʋ�� �����ϴ� ��ü
		
		
			DecimalFormat decimalFormat=new DecimalFormat("###");
			
			txtKo.setTextFormatter(new TextFormatter<>( e ->{
				//1. �����Է��� �����̽������̸� �ٽ� �̺�Ʈ�� �����ش�. 
				if(e.getControlNewText().isEmpty()) { return e;}
				//2. ��ġ����(Ű����ġ�� ��ġ�����ذ���.)
				ParsePosition parsePosition=new ParsePosition(0);
				//���ڸ� �ްڴ�.(3���ڸ�)
				Object object=decimalFormat.parse(e.getControlNewText(),parsePosition );
				int number =Integer.MAX_VALUE;
				try {
					number = Integer.parseInt(e.getControlNewText());
				}catch(NumberFormatException e2) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("�����Է¿��");
					alert.setHeaderText("����(0~100)�� �Է��Ͻÿ�!");
					alert.setContentText("���ڿ��� �ٸ������Էµ����ʴ´�.  ");
					alert.showAndWait();
				}
				if(object == null || e.getControlNewText().length() >=4 || number > 100 ) {
					return null;
				}else {
					return e;
				}
			}));
			
			txtEng.setTextFormatter(new TextFormatter<>( e ->{
				//1. �����Է��� �����̽������̸� �ٽ� �̺�Ʈ�� �����ش�. 
				if(e.getControlNewText().isEmpty()) { return e;}
				//2. ��ġ����(Ű����ġ�� ��ġ�����ذ���.)
				ParsePosition parsePosition=new ParsePosition(0);
				//���ڸ� �ްڴ�.(3���ڸ�)
				Object object=decimalFormat.parse(e.getControlNewText(),parsePosition );
				int number =Integer.MAX_VALUE;
				try {
					number = Integer.parseInt(e.getControlNewText());
				}catch(NumberFormatException e2) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("�����Է¿��");
					alert.setHeaderText("����(0~100)�� �Է��Ͻÿ�!");
					alert.setContentText("���ڿ��� �ٸ������Էµ����ʴ´�.  ");
					alert.showAndWait();
				}
				if(object == null || e.getControlNewText().length() >=4 || number > 100 ) {
					return null;
				}else {
					return e;
				}
			}));
			
			txtMath.setTextFormatter(new TextFormatter<>( e ->{
				//1. �����Է��� �����̽������̸� �ٽ� �̺�Ʈ�� �����ش�. 
				if(e.getControlNewText().isEmpty()) { return e;}
				//2. ��ġ����(Ű����ġ�� ��ġ�����ذ���.)
				ParsePosition parsePosition=new ParsePosition(0);
				//���ڸ� �ްڴ�.(3���ڸ�)
				Object object=decimalFormat.parse(e.getControlNewText(),parsePosition );
				int number =Integer.MAX_VALUE;
				try {
					number = Integer.parseInt(e.getControlNewText());
				}catch(NumberFormatException e2) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("�����Է¿��");
					alert.setHeaderText("����(0~100)�� �Է��Ͻÿ�!");
					alert.setContentText("���ڿ��� �ٸ������Էµ����ʴ´�.  ");
					alert.showAndWait();
				}
				if(object == null || e.getControlNewText().length() >=4 || number > 100 ) {
					return null;
				}else {
					return e;
				}
			}));
			
			txtMusic.setTextFormatter(new TextFormatter<>( e ->{
				//1. �����Է��� �����̽������̸� �ٽ� �̺�Ʈ�� �����ش�. 
				if(e.getControlNewText().isEmpty()) { return e;}
				//2. ��ġ����(Ű����ġ�� ��ġ�����ذ���.)
				ParsePosition parsePosition=new ParsePosition(0);
				//���ڸ� �ްڴ�.(3���ڸ�)
				Object object=decimalFormat.parse(e.getControlNewText(),parsePosition );
				int number =Integer.MAX_VALUE;
				try {
					number = Integer.parseInt(e.getControlNewText());
				}catch(NumberFormatException e2) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("�����Է¿��");
					alert.setHeaderText("����(0~100)�� �Է��Ͻÿ�!");
					alert.setContentText("���ڿ��� �ٸ������Էµ����ʴ´�.  ");
					alert.showAndWait();
				}
				if(object == null || e.getControlNewText().length() >=4 || number > 100 ) {
					return null;
				}else {
					return e;
				}
			}));
			
			txtSic.setTextFormatter(new TextFormatter<>( e ->{
				//1. �����Է��� �����̽������̸� �ٽ� �̺�Ʈ�� �����ش�. 
				if(e.getControlNewText().isEmpty()) { return e;}
				//2. ��ġ����(Ű����ġ�� ��ġ�����ذ���.)
				ParsePosition parsePosition=new ParsePosition(0);
				//���ڸ� �ްڴ�.(3���ڸ�)
				Object object=decimalFormat.parse(e.getControlNewText(),parsePosition );
				int number =Integer.MAX_VALUE;
				try {
					number = Integer.parseInt(e.getControlNewText());
				}catch(NumberFormatException e2) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("�����Է¿��");
					alert.setHeaderText("����(0~100)�� �Է��Ͻÿ�!");
					alert.setContentText("���ڿ��� �ٸ������Էµ����ʴ´�.  ");
					alert.showAndWait();
				}
				if(object == null || e.getControlNewText().length() >=4 || number > 100 ) {
					return null;
				}else {
					return e;
				}
			}));
			
			txtSoc.setTextFormatter(new TextFormatter<>( e ->{
				//1. �����Է��� �����̽������̸� �ٽ� �̺�Ʈ�� �����ش�. 
				if(e.getControlNewText().isEmpty()) { return e;}
				//2. ��ġ����(Ű����ġ�� ��ġ�����ذ���.)
				ParsePosition parsePosition=new ParsePosition(0);
				//���ڸ� �ްڴ�.(3���ڸ�)
				Object object=decimalFormat.parse(e.getControlNewText(),parsePosition );
				int number =Integer.MAX_VALUE;
				try {
					number = Integer.parseInt(e.getControlNewText());
				}catch(NumberFormatException e2) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("�����Է¿��");
					alert.setHeaderText("����(0~100)�� �Է��Ͻÿ�!");
					alert.setContentText("���ڿ��� �ٸ������Էµ����ʴ´�.  ");
					alert.showAndWait();
				}
				if(object == null || e.getControlNewText().length() >=4 || number > 100 ) {
					return null;
				}else {
					return e;
				}
			}));
			
	}
	// ������ư �̺�Ʈ��� �ڵ鷯�Լ�ó��
	private void handleBtnEditAction(ActionEvent event) {
		// formedit.fxml ȭ���� �ε��ؾߵȴ�.
		try {
			// ȭ�鳻��->�� -> ��������(���ν�������)-> �����ָ�ȴ�. �� �����Ծ��.
			Parent root = FXMLLoader.load(getClass().getResource("/view/formedit.fxml"));
			
			// scene(ȭ�鳻��) �����.
			Scene scene = new Scene(root);
			
			Stage editStage=new Stage(StageStyle.UTILITY);
			
			// ++++++++�̺�Ʈ��� �� �ڵ鷯ó��+++++++++++
			// @FXML private TextField txtName -> �̰��� �Ҽ�����. (��Ʋ�ѷ��� ���⶧��)
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

			// ���̺�信�� ���õ� ��ġ���������� observable list���� �� ��ġ�� ã�Ƽ�
			// �ش�� Student ��ü�� �������� �ȴ�.
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
			
			//txtNo �ؽ�Ʈ�ʵ带 read only(�б⸸ ����) �����.(��ȣ,�̸�,�г�,��,����)
			txtNo.setDisable(true);
			txtName.setDisable(true);
			txtYear.setDisable(true);
			txtBan.setDisable(true);
			txtGender.setDisable(true);
			
			
			//����ɿ� �ش�� �̺�Ʈ��� �� �ڵ鷯 ó�����
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
			
			//�����ư �̺�Ʈ��� �� �ڵ鷯 ó�����
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
				
				//����Ÿ���̽� �۾��� �����Ѵ�. 
				//1. ����Ÿ���̽��ؾߵǴ� ����
				Connection con=null;
				PreparedStatement pstmt=null;
				try {
					//1. mysql ����̹� �δ�����Ÿ���̽� ��ü�� �����;� �ȴ�. 
					Class.forName("com.mysql.jdbc.Driver");
					//2. ���̵�� �н����带 ����Ÿ���̽��� ���ӿ�û�� �㰡�޾Ƽ� Ŀ�ؼǰ�ü�� ���´�.
					con = DriverManager.getConnection("jdbc:mysql://localhost/studentdb","root","123456");
					if(con != null) {
						System.out.println("RootControoler.BtnDeleteAction :DB ���Ἲ��");
					}else {
						System.out.println("RootControoler.BtnDeleteAction :DB �������");
					}
					//3. con ��ü�� ������ �������� �����Ҽ��ִ�. (select, insert, update, delete)
					String query = "update gradeTBL set korean=?, english=?, math=?, sic=?, "
							+ "soc=?, music=?, total=?, average=? where no=?";
					//4. �������� �����ϱ����� �غ�
					pstmt=con.prepareStatement(query);
					//5. ����ڵ带 ���������� �������� ? no ��ȣ�� �����Ѵ�. 
					pstmt.setString(1,stu.getKorean());
					pstmt.setString(2,stu.getEnglish());
					pstmt.setString(3,stu.getMath());
					pstmt.setString(4,stu.getSic());
					pstmt.setString(5,stu.getSoc());
					pstmt.setString(6,stu.getMusic());
					pstmt.setString(7,stu.getTotal());
					pstmt.setString(8,stu.getAvg());
					pstmt.setInt(9,stu.getNo());
					
					//5. �������� �����Ѵ�.
					//(�������� �����ؼ� ���ڵ峻�� ������� RecordSet�� �����´�. executeQuery();)
					//(���� �������� ���ุ�Ѵ�. : executeUpdate();) 
					int returnValue=pstmt.executeUpdate();
					
					if(returnValue != 0) {
						//���̺�� obsList �ش�� ��ġ�� ������ ��ü���� �Է��Ѵ�. 
						obsList.set(tableViewSelectedIndex, stu);
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("���� ����");
						alert.setHeaderText(student.getNo() +"�� ���� ����");
						alert.setContentText(student.getName()+"�� �����߽��ϴ�." );
						alert.showAndWait();
					}else {
						throw new Exception("������ ��������");
					}

				} catch (Exception e1) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("���� ���˿��");
					alert.setHeaderText("���� �����߻�! \n RootController.BtnEditAction");
					alert.setContentText("��������: " +e1.getMessage());
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
			
			//��ҹ�ư�� ������ â�� �ݴ´�. 
			btnFormCancel.setOnAction(e-> { editStage.close();	});
			
			// ��������(���ν�������)�� �����.(*���, ��޸���), ��������(��), ��������ũ���������
			// editStage = new Stage(StageStyle.UTILITY);
			editStage.initModality(Modality.WINDOW_MODAL);
			editStage.initOwner(stage);
			editStage.setScene(scene);
			editStage.setTitle("�������α׷� ����â");
			editStage.setResizable(false);
			editStage.show();
		} catch (IndexOutOfBoundsException | IOException e1) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("����â ����");
			alert.setHeaderText("���˿��!");
			alert.setContentText("������������! ");
			alert.showAndWait();
		}

	}

	// ���̺�並 ������ ������ �̺�Ʈ��� �ڵ鷯�Լ�ó��
	private void handleTableViewPressedAction(MouseEvent e) {
		tableViewSelectedIndex = tableView.getSelectionModel().getSelectedIndex();
	}

	// ������ư �̺�Ʈ��� �ڵ鷯�Լ�ó��
	private void handleBtnDeleteAction(ActionEvent event) {
		//1. ����Ÿ���̽��ؾߵǴ� ����
		Connection con=null;
		PreparedStatement pstmt=null;
		try {
			//1. mysql ����̹� �δ�����Ÿ���̽� ��ü�� �����;� �ȴ�. 
			Class.forName("com.mysql.jdbc.Driver");
			//2. ���̵�� �н����带 ����Ÿ���̽��� ���ӿ�û�� �㰡�޾Ƽ� Ŀ�ؼǰ�ü�� ���´�.
			con = DriverManager.getConnection("jdbc:mysql://localhost/studentdb","root","123456");
			if(con != null) {
				System.out.println("RootControoler.BtnDeleteAction :DB ���Ἲ��");
			}else {
				System.out.println("RootControoler.BtnDeleteAction :DB �������");
			}
			//3. con ��ü�� ������ �������� �����Ҽ��ִ�. (select, insert, update, delete)
			String query = "delete from gradeTBL where no = ? ";
			//4. �������� �����ϱ����� �غ�
			pstmt=con.prepareStatement(query);
			//5. ����ڵ带 ���������� �������� ? no ��ȣ�� �����Ѵ�. 
			Student student=obsList.get(tableViewSelectedIndex);
			int no = student.getNo();
			pstmt.setInt(1, no);
			//5. �������� �����Ѵ�.
			//(�������� �����ؼ� ���ڵ峻�� ������� RecordSet�� �����´�. executeQuery();)
			//(���� �������� ���ุ�Ѵ�. : executeUpdate();) 
			int returnValue=pstmt.executeUpdate();
			
			if(returnValue != 0) {
				obsList.remove(tableViewSelectedIndex);
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("���� ����");
				alert.setHeaderText(student.getNo() +"�� ���� ����");
				alert.setContentText(student.getName()+"�� �ȳ�" );
				alert.showAndWait();
			}else {
				throw new Exception("������ ��������");
			}

		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("���� ���˿��");
			alert.setHeaderText("���� �����߻�! \n RootController.BtnDeleteAction");
			alert.setContentText("��������: " +e.getMessage());
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

	// ã���ư �̺�Ʈ��� �ڵ鷯�Լ�ó��
	private void handleBtnSearchAction(ActionEvent e) {
		// ���̺�信 ����ִ� ����Ÿ���� obsList ������ �ϳ��� �����´�.
		try {
			if (txtSearch.getText().trim().equals(""))
				throw new Exception();
			//obsList���� �˻��Ѱ��ε�.. ������ ���̺���(gradeTBL) �O�Ƽ� �����´�. 
//			for (Student student : obsList) {
//				if (student.getName().equals(txtSearch.getText())) {
//					tableView.getSelectionModel().select(student);
//				}
//			}
			//����Ÿ���̽� �������� �����մϴ�. 
			Connection con=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			try {
				//1. mysql ����̹� �δ�����Ÿ���̽� ��ü�� �����;� �ȴ�. 
				Class.forName("com.mysql.jdbc.Driver");
				//2. ���̵�� �н����带 ����Ÿ���̽��� ���ӿ�û�� �㰡�޾Ƽ� Ŀ�ؼǰ�ü�� ���´�.
				con = DriverManager.getConnection("jdbc:mysql://localhost/studentdb","root","123456");
				if(con != null) {
					System.out.println("RootControoler.totalLoadList :DB ���Ἲ��");
				}else {
					System.out.println("RootControoler.totalLoadList :DB �������");
				}
				//3. con ��ü�� ������ �������� �����Ҽ��ִ�. (select, insert, update, delete)
				String query = "select * from gradeTBL where name = ?";
				//4. �������� �����ϱ����� �غ�
				pstmt=con.prepareStatement(query);
				pstmt.setString(1,txtSearch.getText().trim());
				
				//5. �������� �����Ѵ�.(������� ���ڵ峻���� �迭�� �����´�. ) 
				rs=pstmt.executeQuery();
				//6. ResultSet ���� �Ѱ��� �����ͼ� ArrayList�� �����Ѵ�. 
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
				//7. ArrayList�� �ִ°��� ObservableList<Student> obsList �Է��Ѵ�. 
				if(arrayList.size() != 0 ) {
					obsList.clear();
					for(int i=0; i < arrayList.size() ; i++ ) {
						Student s = arrayList.get(i);
						obsList.add(s);
					}
				}
				
			} catch (Exception e1) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("�˻� ���˿��");
				alert.setHeaderText("�˻� �����߻�!");
				alert.setContentText("��������: " +e1.getMessage());
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
			//����Ÿ���̽� ����===============
			
		} catch (Exception event) {
			// ���â�� �������. ��������, ��, ȭ�鳻��(����ȭ��Ŵ)
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("�̸��Է¿��");
			alert.setHeaderText("�̸��� �Է��Ͻÿ�!");
			alert.setContentText("�������� �����ϼ���. ");
			alert.showAndWait();
		}
	}

	// ����������ư �׷� �ʱ�ȭó��
	private void radioButtonGenderInitialize() {
		group = new ToggleGroup();
		rdoMale.setToggleGroup(group);
		rdoFemale.setToggleGroup(group);
		rdoMale.setSelected(true);
	}

	// �гⷹ���� �Է��ϴ� �ʱ�ȭ ó��(1~6�г�)
	private void comboBoxLevelInitialize() {
		ObservableList<String> obsList = FXCollections.observableArrayList();
		obsList.addAll("1", "2", "3", "4", "5", "6");
		cmbLevel.setItems(obsList);
	}

	// ���̺�信 ��Ϲ�ư�̺�Ʈ��� �ڵ鷯�Լ�ó��
	private void handleBtnOkAction(ActionEvent event) {
		//1. ����Ÿ���̽��ؾߵǴ� ����
		Connection con=null;
		PreparedStatement pstmt=null;
		try {
			//1. mysql ����̹� �δ�����Ÿ���̽� ��ü�� �����;� �ȴ�. 
			Class.forName("com.mysql.jdbc.Driver");
			//2. ���̵�� �н����带 ����Ÿ���̽��� ���ӿ�û�� �㰡�޾Ƽ� Ŀ�ؼǰ�ü�� ���´�.
			con = DriverManager.getConnection("jdbc:mysql://localhost/studentdb","root","123456");
			if(con != null) {
				System.out.println("RootControoler.BtnDeleteAction :DB ���Ἲ��");
			}else {
				System.out.println("RootControoler.BtnDeleteAction :DB �������");
			}
			//3. con ��ü�� ������ �������� �����Ҽ��ִ�. (select, insert, update, delete)
			String query = "insert into gradeTBL values(null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
			//4. �������� �����ϱ����� �غ�
			pstmt=con.prepareStatement(query);
			//5. ����ڵ带 ���������� �������� ? no ��ȣ�� �����Ѵ�. 
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
			//5. �������� �����Ѵ�.
			//(�������� �����ؼ� ���ڵ峻�� ������� RecordSet�� �����´�. executeQuery();)
			//(���� �������� ���ุ�Ѵ�. : executeUpdate();) 
			int returnValue=pstmt.executeUpdate();
			
			if(returnValue != 0) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("���� ����");
				alert.setHeaderText(s.getName() +"�� ���� ����");
				alert.setContentText(s.getName()+"�� �氡�氡" );
				alert.showAndWait();
				
				obsList.clear();
				totalLoadList();
				
			}else {
				throw new Exception(s.getName()+"�� ���Կ� ��������");
			}

		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("���� ���˿��");
			alert.setHeaderText("���� �����߻�! \n RootController.BtnOkAction");
			alert.setContentText("��������: " +e.getMessage());
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
//			// ���â�� �������. ��������, ��, ȭ�鳻��(����ȭ��Ŵ)
//			Alert alert = new Alert(AlertType.ERROR);
//			alert.setTitle("����Է¹����߻�");
//			alert.setHeaderText("Student ��ü ������ ���ּ���.!");
//			alert.setContentText("�������� �����ϼ���. ");
//			alert.showAndWait();
//		}
	}

	// �ʱ�ȭ��ư�̺�Ʈ��� �ڵ鷯�Լ�ó��
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

	// ��չ�ư�̺�Ʈ��� �ڵ鷯�Լ�ó��
	private void handleBtnAvgAction(ActionEvent e) {
		try {
			double avg = Integer.parseInt(txtTotal.getText()) / 6.0;
			txtAvg.setText(String.format("%.1f", avg));
		} catch (NumberFormatException event) {
			// ���â�� �������. ��������, ��, ȭ�鳻��(����ȭ��Ŵ)
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("�����Է¿��");
			alert.setHeaderText("������ �Է��� �ȵƾ�� !");
			alert.setContentText("�������� �����ϼ���. ");
			alert.showAndWait();
		}
	}

	// ������ư�̺�Ʈ��� �ڵ鷯�Լ�ó��(6�������ջ����������Ѵ�.)
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
			// ���â�� �������. ��������, ��, ȭ�鳻��(����ȭ��Ŵ)
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("�����Է¿��");
			alert.setHeaderText("������ �Է��Ͻÿ�!");
			alert.setContentText("�������� �����ϼ���. ");
			alert.showAndWait();
		}
	}

	// ���̺��UI��ü �÷��ʱ�ȭ����(�÷��� 12���� ����� ->Student Ŭ�����ʵ�Ϳ���)
	private void tableViewColumnInitialize() {
		TableColumn colNo = new TableColumn("NO");
		colNo.setMaxWidth(40);
		colNo.setCellValueFactory(new PropertyValueFactory("no"));
		
		TableColumn colName = new TableColumn("����");
		colName.setMaxWidth(60);
		colName.setCellValueFactory(new PropertyValueFactory("name"));

		TableColumn colLevel = new TableColumn("�г�");
		colLevel.setMaxWidth(30);
		colLevel.setCellValueFactory(new PropertyValueFactory("level"));

		TableColumn colBan = new TableColumn("��");
		colBan.setMaxWidth(30);
		colBan.setCellValueFactory(new PropertyValueFactory<>("ban"));

		TableColumn colGender = new TableColumn("����");
		colGender.setMaxWidth(40);
		colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));

		TableColumn colKorean = new TableColumn("����");
		colKorean.setMaxWidth(40);
		colKorean.setCellValueFactory(new PropertyValueFactory<>("korean"));

		TableColumn colEnglish = new TableColumn("����");
		colEnglish.setMaxWidth(40);
		colEnglish.setCellValueFactory(new PropertyValueFactory<>("english"));

		TableColumn colMath = new TableColumn("����");
		colMath.setMaxWidth(40);
		colMath.setCellValueFactory(new PropertyValueFactory<>("math"));

		TableColumn colSic = new TableColumn("����");
		colSic.setMaxWidth(40);
		colSic.setCellValueFactory(new PropertyValueFactory<>("sic"));

		TableColumn colSoc = new TableColumn("��ȸ");
		colSoc.setMaxWidth(40);
		colSoc.setCellValueFactory(new PropertyValueFactory<>("soc"));

		TableColumn colMusic = new TableColumn("����");
		colMusic.setMaxWidth(40);
		colMusic.setCellValueFactory(new PropertyValueFactory<>("music"));

		TableColumn colTotal = new TableColumn("����");
		colTotal.setMaxWidth(40);
		colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

		TableColumn colAvg = new TableColumn("���");
		colAvg.setMaxWidth(50);
		colAvg.setCellValueFactory(new PropertyValueFactory<>("avg"));

		tableView.getColumns().addAll(colNo, colName, colLevel, colBan, colGender, colKorean, colEnglish, colMath, colSic,
				colSoc, colMusic, colTotal, colAvg);
		tableView.setItems(obsList);

		DecimalFormat format = new DecimalFormat("###");
		// ���� �Է½� ���� ���� �̺�Ʈ ó��
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
