package request

type RegisterKaryawan struct {
	NamaKaryawan string `json:"nama_karyawan"`
	Nik          string `json:"nik"`
	Username     string `json:"username"`
}
