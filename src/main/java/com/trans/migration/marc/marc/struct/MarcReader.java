/*
 * @author 이태일
 * @Copyright(c) 2017 by (주)이씨오. All rights reserved.
 */

package com.trans.migration.marc.marc.struct;

/**
* 마크의 리더를 관리하는 클래스이다.
* @author Written By 이태일 【taeil.lee@eco.co.kr】
*/
final class MarcReader
{
	/**
	 * 마크의 리더
	 */
	private String reader;

	/**
	 * @param reader 마크의 리더
	 */
	public void setReader(String reader)
	{
		this.reader = reader;
	}

	/**
	 * 마크의 리더를 가져온다.
	 * @return 마크의 리더
	 */
	public String gerReader()
	{
		return this.reader;
	}
}
