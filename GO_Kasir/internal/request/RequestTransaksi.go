package request

type RequestBiayaTransaksi struct {
	TotalPrice        float64 `json:"total_price"`
	NominalPembayaran float64 `json:"nominal_pembayaran"`
	CodeVoucer        string  `json:"code_voucer"`
	IdMember          string  `json:"id_Member"`
}

type RequestTransaksi struct {
	TotalPrice        float64 `json:"total_price"`
	NominalPembayaran float64 `json:"nominal_pembayaran"`
	Ppn               float64 `json:"ppn"`
	Kembalian         float64 `json:"kembalian"`
	KaryawanID        float64 `json:"karyawan_id"`
	CodeVoucer        string  `json:"code_voucer"`
	Point             uint    `json:"point"`
	MemberID          int     `json:"member_id"`
}
