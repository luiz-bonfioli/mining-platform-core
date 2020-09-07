package com.mining.platform.core.infrastructure.changelog;

/**
 * 
 * @author luiz.bonfioli
 *
 */
public enum ExecutionStatus {

	SUCCESS((byte) 0x00),
	ERROR((byte) 0x01);
	
	private byte execution;

	ExecutionStatus(byte execution) {
		this.execution = execution;
	}

	public byte getExecution() {
		return execution;
	}

	/**
	 * 
	 * @param execution
	 * @return
	 */
	public static ExecutionStatus valueOf(byte execution) {

		for (ExecutionStatus executionStatus : ExecutionStatus.values()) {
			if (Byte.compare(executionStatus.getExecution(), execution) == 0) {
				return executionStatus;
			}
		}

		return null;

	}
}
