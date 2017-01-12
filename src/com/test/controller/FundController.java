package com.test.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.BeanToCsv;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.test.IConstants;
import com.test.bo.BenchMarkBO;
import com.test.bo.FundBO;
import com.test.bo.MonthlyOutPerformanceBO;
import com.test.bo.ReturnSeriesBO;


@Controller
@RequestMapping("/")
public class FundController {

	protected static Log logger = LogFactory.getLog(FundController.class);
	@Autowired
	ServletContext servletContext;
	@Autowired
    private MessageSource messageSource;
	 
	@RequestMapping("/")
	public ModelAndView showHomePage(HttpServletRequest request,
			HttpServletResponse response, Map model) throws IOException {
		logger.info("real path = "+servletContext.getRealPath("/"));
		System.out.println("real path = "+servletContext.getRealPath("/"));
		System.out.println("property message = "+messageSource.getMessage(IConstants.CSV_FILE_PATH, null, Locale.ENGLISH));
		//System.out.println(messageSource.getMessage(IConstants.DATE_FORMAT_1,null);
		List<FundBO> funds = getFundsFromFrondCSV();
		List<BenchMarkBO> benchmarks = getBenchmarksFromBenchMarkCSV();
		List<ReturnSeriesBO> fundReturnSeries = getReturnSeries(messageSource.getMessage(IConstants.FUND_RETURN_SERIES_CSV_FILE_NAME, null, Locale.ENGLISH));
		List<ReturnSeriesBO> benchmarkReturnSeries = getReturnSeries(messageSource.getMessage(IConstants.BENCHMARK_RETURN_SERIES_CSV_FILE_NAME, null, Locale.ENGLISH));
		
		 CSVWriter csvWriter = null;
		try {
			
			response.setHeader("Content-Disposition", "attachment; filename=" + messageSource.getMessage(IConstants.OUT_PERFORMANCE_CSV_FILE_NAME, null, Locale.ENGLISH) + ".csv");
            response.setContentType("text/csv");
            OutputStreamWriter osw = new OutputStreamWriter(response.getOutputStream(), "UTF-8");

            csvWriter = new CSVWriter(osw);
            BeanToCsv bc = new BeanToCsv();
			 
			 List outPerformanceList = getOutPerformanceList(funds, benchmarks, fundReturnSeries, benchmarkReturnSeries);
			
			 ColumnPositionMappingStrategy mappingStrategy = 
	            		new ColumnPositionMappingStrategy();
	            //Set mappingStrategy type to Employee Type
	            mappingStrategy.setType(MonthlyOutPerformanceBO.class);
	            //Fields in Employee Bean
	            String[] columns = new String[]{messageSource.getMessage(IConstants.FUND_NAME, null, Locale.ENGLISH),messageSource.getMessage(IConstants.DATE, null, Locale.ENGLISH),messageSource.getMessage(IConstants.EXCESS, null, Locale.ENGLISH),messageSource.getMessage(IConstants.OUT_PERFORMANCE, null, Locale.ENGLISH),messageSource.getMessage(IConstants.RETURNS, null, Locale.ENGLISH),messageSource.getMessage(IConstants.RANK, null, Locale.ENGLISH)};
	            //Setting the colums for mappingStrategy
	            mappingStrategy.setColumnMapping(columns);
	            //Writing empList to csv file
	            bc.write(mappingStrategy,csvWriter,outPerformanceList);
	            
	            //csvWriter.writeAll(outPerformanceList);
	           
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		finally
		{
			try
			{
				//closing the writer
				csvWriter.close();
				response.getOutputStream().flush();
				response.getOutputStream().close();
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
		
		return null;
		
	}
	
	
	public  List getFundsFromFrondCSV(){
		
		CSVReader reader = null;
		List<FundBO> funds = new ArrayList<FundBO>();
		try {
			reader = new CSVReader(new FileReader(servletContext.getRealPath("/")+messageSource.getMessage(IConstants.CSV_FILE_PATH, null, Locale.ENGLISH)+messageSource.getMessage(IConstants.FUND_CSV_FILE_NAME, null, Locale.ENGLISH)+".csv"), CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, 1);
			
			
			ColumnPositionMappingStrategy<FundBO> beanStrategy = new ColumnPositionMappingStrategy<FundBO>();
			beanStrategy.setType(FundBO.class);
			beanStrategy.setColumnMapping(new String[] {messageSource.getMessage(IConstants.FUND_CODE, null, Locale.ENGLISH),messageSource.getMessage(IConstants.FUND_NAME, null, Locale.ENGLISH),messageSource.getMessage(IConstants.BENCHMARK_CODE, null, Locale.ENGLISH)});
			
			CsvToBean<FundBO> csvToBean = new CsvToBean<FundBO>();
			funds = csvToBean.parse(beanStrategy, reader);
	
			Iterator<FundBO> iterator = funds.iterator();
			while (iterator.hasNext()) {
				
				FundBO bo = (FundBO)iterator.next();
			}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		finally
		{
			try
			{
				//closing the reader
				reader.close();
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
		
		return funds;
	}
	
	public  List getBenchmarksFromBenchMarkCSV(){
		
			CSVReader reader = null;
			List<BenchMarkBO> benchmarks = new ArrayList<BenchMarkBO>();
			try {
				reader = new CSVReader(new FileReader(servletContext.getRealPath("/")+messageSource.getMessage(IConstants.CSV_FILE_PATH, null, Locale.ENGLISH)+messageSource.getMessage(IConstants.BENCHMARK_CSV_FILE_NAME, null, Locale.ENGLISH)+".csv"), CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, 1);
			
				ColumnPositionMappingStrategy<BenchMarkBO> beanStrategy = new ColumnPositionMappingStrategy<BenchMarkBO>();
				beanStrategy.setType(BenchMarkBO.class);
				beanStrategy.setColumnMapping(new String[] {messageSource.getMessage(IConstants.BENCHMARK_CODE, null, Locale.ENGLISH),messageSource.getMessage(IConstants.BENCHMARK_NAME, null, Locale.ENGLISH)});
				
				CsvToBean<BenchMarkBO> csvToBean = new CsvToBean<BenchMarkBO>();
				benchmarks = csvToBean.parse(beanStrategy, reader);
				Iterator<BenchMarkBO> iterator = benchmarks.iterator();
				while (iterator.hasNext()) {
					
					BenchMarkBO bo = (BenchMarkBO)iterator.next();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally
			{
				try
				{
					//closing the reader
					reader.close();
				}
				catch(Exception ee)
				{
					ee.printStackTrace();
				}
			}
			return benchmarks;
		}
	
	
	
	public  List getReturnSeries(String filename){
		
		CSVReader reader = null;
		List<ReturnSeriesBO> returnSeries = new ArrayList<ReturnSeriesBO>();
		try {
			reader = new CSVReader(new FileReader(servletContext.getRealPath("/")+messageSource.getMessage(IConstants.CSV_FILE_PATH, null, Locale.ENGLISH)+filename+".csv"), CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, 1);
			
			ColumnPositionMappingStrategy<ReturnSeriesBO> beanStrategy = new ColumnPositionMappingStrategy<ReturnSeriesBO>();
			beanStrategy.setType(ReturnSeriesBO.class);
			beanStrategy.setColumnMapping(new String[] {messageSource.getMessage(IConstants.CODE, null, Locale.ENGLISH),messageSource.getMessage(IConstants.DATE, null, Locale.ENGLISH), messageSource.getMessage(IConstants.RETURNS, null, Locale.ENGLISH)});
			
			CsvToBean<ReturnSeriesBO> csvToBean = new CsvToBean<ReturnSeriesBO>();
			returnSeries = csvToBean.parse(beanStrategy, reader);
			//System.out.println(returnSeries.size());
			Iterator<ReturnSeriesBO> iterator = returnSeries.iterator();
			while (iterator.hasNext()) {
				
				ReturnSeriesBO bo = (ReturnSeriesBO)iterator.next();
				
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try
			{
				//closing the reader
				reader.close();
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
		return returnSeries;
	}
	
	public  List getOutPerformanceList(List funds, List benchmarks, List fundReturnSeries, List benchReturnSeries){
		
		List<MonthlyOutPerformanceBO> outPerformanceList = new ArrayList<MonthlyOutPerformanceBO>();
		
		
		if(fundReturnSeries !=null && fundReturnSeries.size()>0){
			
			for(int i=0;i<fundReturnSeries.size();i++){
				
				ReturnSeriesBO fundReturnSeriesBO = (ReturnSeriesBO)fundReturnSeries.get(i);
				MonthlyOutPerformanceBO outPerformanceBO = new MonthlyOutPerformanceBO();
				
				FundBO fundBO = getFundObject(fundReturnSeriesBO.getCode(), funds);
				outPerformanceBO.setFundName(getFundName(fundReturnSeriesBO.getCode(), funds));
				outPerformanceBO.setDate(fundReturnSeriesBO.getDate());
				outPerformanceBO.setReturns(fundReturnSeriesBO.getReturns());
				if(benchReturnSeries !=null && benchReturnSeries.size()>0){
					for(int j=0;j<benchReturnSeries.size();j++){
						ReturnSeriesBO benchReturnSeriesBO = (ReturnSeriesBO)benchReturnSeries.get(j);
						if(((fundBO.getBenchmarkCode()).equals(benchReturnSeriesBO.getCode()))&& (getDate(fundReturnSeriesBO.getDate()).equals(getDate(benchReturnSeriesBO.getDate())))){
							outPerformanceBO.setExcess(getExcessDifference(fundReturnSeriesBO.getReturns(), benchReturnSeriesBO.getReturns()));
							outPerformanceBO.setOutPerformance(getOutPerformanceText(outPerformanceBO.getExcess()));
							break;
						}else{
							
							outPerformanceBO.setExcess(fundReturnSeriesBO.getReturns());
							outPerformanceBO.setOutPerformance(getOutPerformanceText(outPerformanceBO.getReturns()));
						}
							
							
					}
					
				}else{
					
					outPerformanceBO.setExcess(fundReturnSeriesBO.getReturns());
					outPerformanceBO.setOutPerformance(getOutPerformanceText(outPerformanceBO.getReturns()));
					
				}
				outPerformanceList.add(outPerformanceBO);
				
				Collections.sort(outPerformanceList, new Comparator<MonthlyOutPerformanceBO>(){
				     public int compare(MonthlyOutPerformanceBO o1, MonthlyOutPerformanceBO o2){
				         if(o1.getReturns() == o2.getReturns())
				             return 0;
				         return o1.getReturns() > o2.getReturns() ? -1 : 1;
				     }
				});
				
				Collections.sort(outPerformanceList, new Comparator<MonthlyOutPerformanceBO>()
						{
						  public int compare(MonthlyOutPerformanceBO o1, MonthlyOutPerformanceBO o2) {
						   // SimpleDateFormat sdf = new SimpleDateFormat("YOUR-DATE-PATTERN");
						    Date d1 = getDateObj(o1.getDate());
						    Date d2 = getDateObj(o2.getDate());
						    return d2.compareTo(d1);
						  }
						});
				
				
				setRank(outPerformanceList);
			}
			
		}
		return outPerformanceList;
	}
	
	public  List setRank(List outPerformanceList){
		String date= "";
		int rank=0;
		List performanceList = new ArrayList<>();
		for(int i=0;i<outPerformanceList.size();i++){
			
			MonthlyOutPerformanceBO bo = (MonthlyOutPerformanceBO)outPerformanceList.get(i);
			
			if(!date.equals("") && date.equals(bo.getDate())){
				rank = rank+1;
				bo.setRank(rank);
				performanceList.add(bo);
			}else{
				date = bo.getDate();
				i--;
				rank=0;
			}
		}
		
		return performanceList;
	}
	
	public  String getFundName(String Code, List<FundBO> funds){
		for(FundBO fundBO :funds) { 
			   if(fundBO.getFundCode().equals(Code)) { 
			       //found it!
				   return fundBO.getFundName();
			   }
			}
		return "";
	}
	
	public  FundBO getFundObject(String Code, List<FundBO> funds){
		for(FundBO fundBO :funds) { 
			   if(fundBO.getFundCode().equals(Code)) { 
			       //found it!
				   return fundBO;
			   }
			}
		return null;
	}
	
	public  String getOutPerformanceText(double excess){
		if(excess<-1)
			return messageSource.getMessage(IConstants.UNDER_PERFORMANCE_TEXT, null, Locale.ENGLISH);
		else if(excess>1)
			return messageSource.getMessage(IConstants.OUT_PERFORMANCE_TEXT, null, Locale.ENGLISH);
		else
			return "";
	}
	
	public  double getExcessDifference(double fundReturns, double bechReturns){
		double excess;
		
		excess = fundReturns - bechReturns;
		return excess;
	}
	
	public  String getDate(String date){
		
		String dateStr = null;
		SimpleDateFormat df = new SimpleDateFormat(messageSource.getMessage(IConstants.DATE_FORMAT_1, null, Locale.ENGLISH));
		SimpleDateFormat dt1 = new SimpleDateFormat(messageSource.getMessage(IConstants.DATE_FORMAT_1, null, Locale.ENGLISH));
		if (date.matches("([0-9]{2})/([0-9]{2})/([0-9]{4})"))
			df = new SimpleDateFormat(messageSource.getMessage(IConstants.DATE_FORMAT_1, null, Locale.ENGLISH));
		else if (date.matches("([0-9]{2})-([0-9]{2})-([0-9]{4})"))
			df = new SimpleDateFormat(messageSource.getMessage(IConstants.DATE_FORMAT_2, null, Locale.ENGLISH));
		else if (date.matches("([0-9]{4})-([0-9]{2})-([0-9]{2})"))
			df = new SimpleDateFormat(messageSource.getMessage(IConstants.DATE_FORMAT_3, null, Locale.ENGLISH));
		try {
			Date dateObj = df.parse(date);
			dateStr = dt1.format(dateObj);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return dateStr;
		
	}
	
public  Date getDateObj(String date){
		
		Date dateObj =null;
		SimpleDateFormat df = new SimpleDateFormat(messageSource.getMessage(IConstants.DATE_FORMAT_1, null, Locale.ENGLISH));
		SimpleDateFormat dt1 = new SimpleDateFormat(messageSource.getMessage(IConstants.DATE_FORMAT_1, null, Locale.ENGLISH));
		if (date.matches("([0-9]{2})/([0-9]{2})/([0-9]{4})"))
			df = new SimpleDateFormat(messageSource.getMessage(IConstants.DATE_FORMAT_1, null, Locale.ENGLISH));
		else if (date.matches("([0-9]{2})-([0-9]{2})-([0-9]{4})"))
			df = new SimpleDateFormat(messageSource.getMessage(IConstants.DATE_FORMAT_2, null, Locale.ENGLISH));
		else if (date.matches("([0-9]{4})-([0-9]{2})-([0-9]{2})"))
			df = new SimpleDateFormat(messageSource.getMessage(IConstants.DATE_FORMAT_3, null, Locale.ENGLISH));
		try {
			dateObj = df.parse(date);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return dateObj;
		
	}

}
