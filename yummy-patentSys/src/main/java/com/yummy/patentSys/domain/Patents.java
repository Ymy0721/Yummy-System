package com.yummy.patentSys.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.yummy.common.annotation.Excel;
import com.yummy.common.core.domain.BaseEntity;

/**
 * 专利数据对象 patents
 *
 * @author yummy
 * @date 2024-11-06
 */
public class Patents extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 申请号 */
    @Excel(name = "申请号")
    private String ApplicationNumber;

    /** 公开号 */
    @Excel(name = "公开号")
    private String PublicationNumber;

    /** 申请日 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "申请日", width = 30, dateFormat = "yyyy-MM-dd")
    private Date ApplicationDate;

    /** 公开日 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "公开日", width = 30, dateFormat = "yyyy-MM-dd")
    private Date PublicationDate;

    /** IPC号 */
    @Excel(name = "IPC号")
    private String IPCClassification;

    /** 申请人 */
    @Excel(name = "申请人")
    private String Applicant;

    /** 发明人 */
    @Excel(name = "发明人")
    private String Inventor;

    /** 发明名称 */
    @Excel(name = "发明名称")
    private String InventionName;

    /** 邮编 */
    @Excel(name = "邮编")
    private String ApplicantPostalCode;

    /** 代理人 */
    @Excel(name = "代理人")
    private String Agent;

    /** 代理机构 */
    @Excel(name = "代理机构")
    private String Agency;

    /** 文献类型 */
    @Excel(name = "文献类型")
    private String DocumentType;

    /** 国别 */
    @Excel(name = "国别")
    private String ApplicantCountry;

    /** 摘要 */
    @Excel(name = "摘要")
    private String Abstract;

    /** 专利唯一标识符 */
    private Long patentId;

    public void setApplicationNumber(String ApplicationNumber)
    {
        this.ApplicationNumber = ApplicationNumber;
    }

    public String getApplicationNumber()
    {
        return ApplicationNumber;
    }
    public void setPublicationNumber(String PublicationNumber)
    {
        this.PublicationNumber = PublicationNumber;
    }

    public String getPublicationNumber()
    {
        return PublicationNumber;
    }
    public void setApplicationDate(Date ApplicationDate)
    {
        this.ApplicationDate = ApplicationDate;
    }

    public Date getApplicationDate()
    {
        return ApplicationDate;
    }
    public void setPublicationDate(Date PublicationDate)
    {
        this.PublicationDate = PublicationDate;
    }

    public Date getPublicationDate()
    {
        return PublicationDate;
    }
    public void setIPCClassification(String IPCClassification)
    {
        this.IPCClassification = IPCClassification;
    }

    public String getIPCClassification()
    {
        return IPCClassification;
    }
    public void setApplicant(String Applicant)
    {
        this.Applicant = Applicant;
    }

    public String getApplicant()
    {
        return Applicant;
    }
    public void setInventor(String Inventor)
    {
        this.Inventor = Inventor;
    }

    public String getInventor()
    {
        return Inventor;
    }
    public void setInventionName(String InventionName)
    {
        this.InventionName = InventionName;
    }

    public String getInventionName()
    {
        return InventionName;
    }
    public void setApplicantPostalCode(String ApplicantPostalCode)
    {
        this.ApplicantPostalCode = ApplicantPostalCode;
    }

    public String getApplicantPostalCode()
    {
        return ApplicantPostalCode;
    }
    public void setAgent(String Agent)
    {
        this.Agent = Agent;
    }

    public String getAgent()
    {
        return Agent;
    }
    public void setAgency(String Agency)
    {
        this.Agency = Agency;
    }

    public String getAgency()
    {
        return Agency;
    }
    public void setDocumentType(String DocumentType)
    {
        this.DocumentType = DocumentType;
    }

    public String getDocumentType()
    {
        return DocumentType;
    }
    public void setApplicantCountry(String ApplicantCountry)
    {
        this.ApplicantCountry = ApplicantCountry;
    }

    public String getApplicantCountry()
    {
        return ApplicantCountry;
    }
    public void setAbstract(String Abstract)
    {
        this.Abstract = Abstract;
    }

    public String getAbstract()
    {
        return Abstract;
    }
    public void setPatentId(Long patentId)
    {
        this.patentId = patentId;
    }

    public Long getPatentId()
    {
        return patentId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("ApplicationNumber", getApplicationNumber())
            .append("PublicationNumber", getPublicationNumber())
            .append("ApplicationDate", getApplicationDate())
            .append("PublicationDate", getPublicationDate())
            .append("IPCClassification", getIPCClassification())
            .append("Applicant", getApplicant())
            .append("Inventor", getInventor())
            .append("InventionName", getInventionName())
            .append("ApplicantPostalCode", getApplicantPostalCode())
            .append("Agent", getAgent())
            .append("Agency", getAgency())
            .append("DocumentType", getDocumentType())
            .append("ApplicantCountry", getApplicantCountry())
            .append("Abstract", getAbstract())
            .append("patentId", getPatentId())
            .toString();
    }
}
