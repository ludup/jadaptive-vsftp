package com.jadaptive.plugins.ssh.vsftp.stats;

import com.sshtools.common.util.IOUtils;

public interface StatsService {

	static final long GIGABYTE = IOUtils.fromByteSize("1GB");
	
	static final String SFTP_FS_OUT = "sftp_rnd_out";

	static final String SFTP_FS_IN = "sftp_rnd_in";

	static final String SFTP_UPLOAD = "sftp_upload";

	static final String SCP_UPLOAD = "scp_upload";

	static final String SFTP_DOWNLOAD = "sftp_download";

	static final String SCP_DOWNLOAD = "scp_download";

	static final String SSHD_OUT = "sshd_out";

	static final String SSHD_IN = "sshd_in";
	
	static final String HTTPS_UPLOAD = "https_upload";
	
	static final String HTTPS_DOWNLOAD = "https_download";
	
	static final String SFTP_DELETE = "sftp_delete";

	static final String SFTP_SETSTAT = "sftp_set_stat";

	static final String SFTP_SYMLINKED = "sftp_symlink";

	static final String SFTP_TOUCHED = "sftp_touched";

	static final String SFTP_RENAME = "sftp_rename";

	static final String SFTP_DIR_CREATED = "sftp_mkdir";

	static final String SFTP_DIR_DELETED = "sftp_dir_deleted";
	
	static final String SFTP_DIR_OPEN = "sftp_dir_opened";
	
	static final String SFTP_DIR_CLOSED = "sftp_dir_closed";
	
	static final String SFTP_DIR_LISTING = "sftp_dir";
}
