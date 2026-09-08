package com.ai_driven.back_end.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "TODO_TABLE")
public class TodoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "P_KEY")
	private Integer pKey;

	@Column(name = "TASK", nullable = false, length = 100)
	private String task;

	@Column(name = "REGISTED_DATE")
	private LocalDate registedDate;

	@Column(name = "LIMIT_DATE")
	private LocalDate limitDate;

	@Column(name = "MEMO", length = 500)
	private String memo;

	@Column(name = "CREATE_DATE", nullable = false)
	private LocalDate createDate;

	@Column(name = "UPDATE_DATE", nullable = false)
	private LocalDate updateDate;

	public Integer getPKey() {
		return pKey;
	}

	public void setPKey(Integer pKey) {
		this.pKey = pKey;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public LocalDate getRegistedDate() {
		return registedDate;
	}

	public void setRegistedDate(LocalDate registedDate) {
		this.registedDate = registedDate;
	}

	public LocalDate getLimitDate() {
		return limitDate;
	}

	public void setLimitDate(LocalDate limitDate) {
		this.limitDate = limitDate;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public LocalDate getCreateDate() {
		return createDate;
	}

	public void setCreateDate(LocalDate createDate) {
		this.createDate = createDate;
	}

	public LocalDate getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(LocalDate updateDate) {
		this.updateDate = updateDate;
	}

}
