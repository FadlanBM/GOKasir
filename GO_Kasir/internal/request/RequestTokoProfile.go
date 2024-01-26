package request

type TokoProfileRequest struct {
	NameToko string `json:"name_toko"`
	Npwp     string `json:"npwp"`
	Alamat   string `json:"alamat"`
	Email    string `json:"email"`
	Telp     string `json:"telp"`
}
