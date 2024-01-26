package request

type RegisterKaryawanRequest struct {
	Nik          string `json:"nik"`
	Nama_Petugas string `json:"nama_petugas"`
	Username     string `json:"username"`
	Password     string `json:"password"`
	NamaToko     string `json:"toko_name"`
	Telp         string `json:"telp"`
}

type UpdateKaryawanRequest struct {
	Nik          string `json:"nik"`
	Nama_Petugas string `json:"nama_petugas"`
	Username     string `json:"username"`
	NamaToko     string `json:"toko_name"`
	Telp         string `json:"telp"`
}
type UpdatePassRequest struct {
	PasswordOld string `json:"password_old"`
	PasswordNew string `json:"password_new"`
}
