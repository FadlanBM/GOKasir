package Transaksi

import (
	"errors"
	"github.com/ProjectLSP/config"
	"github.com/ProjectLSP/internal/helper"
	"github.com/ProjectLSP/internal/models"
	"github.com/ProjectLSP/internal/request"
	"github.com/ProjectLSP/internal/response"
	"github.com/gofiber/fiber/v2"
	"gorm.io/gorm"
	"strconv"
)

// Index godoc
// @Tags Crud Transaksi
// @Accept json
// @Produce json
// @Param id path string true "Id Petugas"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/transaksi/{id} [get]
func Index(c *fiber.Ctx) error {
	var transaksi []models.Transaksi
	var member models.Member
	var voucher models.Voucer
	var karyawan models.Karyawan

	idParam := c.Params("id")

	id, err := strconv.Atoi(idParam)
	if err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	if err := config.DB.Find(&transaksi, "karyawan_id = ?", id).Error; err != nil {
		return c.Status(500).JSON(fiber.Map{"Message": err.Error(), "Status": "Internal Server Error"})
	}
	if len(transaksi) == 0 {
		return c.Status(404).JSON(fiber.Map{"Status": "Error", "Message": "Record not found"})
	}

	var res []response.TransaksiResponse

	for _, v := range transaksi {
		if err := config.DB.First(&karyawan, v.KaryawanID).Error; err != nil {
			handleRecordNotFoundError(c, err, "Karyawan")
		}

		if err := config.DB.First(&member, v.MemberID).Error; err != nil {
			handleRecordNotFoundError(c, err, "Member")
		}
		if err := config.DB.First(&voucher, v.VoucerID).Error; err != nil {
			handleRecordNotFoundError(c, err, "Voucher")
		}
		transaksiResponse := response.TransaksiResponse{
			ID:                v.ID,
			TotalPrice:        v.TotalPrice,
			NominalPembayaran: v.NominalTunai,
			Ppn:               v.PPN,
			Kembalian:         v.Kembalian,
			NamaKaryawan:      karyawan.Nama_Karyawan,
			CodeVoucer:        voucher.Code,
			MemberName:        member.Name,
		}
		res = append(res, transaksiResponse)
	}

	return c.Status(200).JSON(fiber.Map{"Message": "Success", "Data": res})
}

// IndexDetail godoc
// @Tags Crud Transaksi
// @Accept json
// @Produce json
// @Param id path string true "Id Transaksi"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/transaksi/detail/{id} [get]
func IndexDetail(c *fiber.Ctx) error {
	var transaksi []models.Transaksi
	var member models.Member
	var voucher models.Voucer
	var karyawan models.Karyawan

	idParam := c.Params("id")

	id, err := strconv.Atoi(idParam)
	if err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	if err := config.DB.First(&transaksi, id).Error; err != nil {
		return c.Status(500).JSON(fiber.Map{"Message": err.Error(), "Status": "Internal Server Error"})
	}
	if len(transaksi) == 0 {
		return c.Status(404).JSON(fiber.Map{"Status": "Error", "Message": "Record not found"})
	}

	var res []response.TransaksiResponse

	for _, v := range transaksi {
		if err := config.DB.First(&karyawan, v.KaryawanID).Error; err != nil {
			handleRecordNotFoundError(c, err, "Karyawan")
		}

		if err := config.DB.First(&member, v.MemberID).Error; err != nil {
			handleRecordNotFoundError(c, err, "Member")
		}
		if err := config.DB.First(&voucher, v.VoucerID).Error; err != nil {
			handleRecordNotFoundError(c, err, "Voucher")
		}
		transaksiResponse := response.TransaksiResponse{
			ID:                v.ID,
			TotalPrice:        v.TotalPrice,
			NominalPembayaran: v.NominalTunai,
			Ppn:               v.PPN,
			Kembalian:         v.Kembalian,
			NamaKaryawan:      karyawan.Nama_Karyawan,
			CodeVoucer:        voucher.Code,
			MemberName:        member.Name,
		}
		res = append(res, transaksiResponse)
	}

	return c.Status(200).JSON(fiber.Map{"Message": "Success", "Data": res})
}

func handleRecordNotFoundError(c *fiber.Ctx, err error, modelName string) error {
	if errors.Is(err, gorm.ErrRecordNotFound) {
		return c.Status(404).JSON(fiber.Map{"Status": "Error", "Message": modelName + " not found"})
	}
	return c.Status(500).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
}

// DetailQR godoc
// @Tags Crud Transaksi
// @Accept json
// @Produce json
// @Param code path string true "Barang Code"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/transaksi/WithQr/{code} [get]
func DetailQR(c *fiber.Ctx) error {
	codeParam := c.Params("code")

	code, err := strconv.Atoi(codeParam)
	if err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	var barang []models.Barang
	if err := config.DB.First(&barang, "code_barang = ?", code).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(404).JSON(fiber.Map{"Status": "Error", "Message": "Record not found"})
		}
		return c.Status(500).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	var res []response.BarangResponse

	for _, v := range barang {
		voucerResponse := response.BarangResponse{
			ID:         v.ID,
			Name:       v.Name,
			CodeBarang: v.CodeBarang,
			Merek:      v.Merek,
			Tipe:       v.Tipe,
			Price:      v.Price,
			Stock:      v.Stock,
		}
		res = append(res, voucerResponse)
	}
	return c.Status(200).JSON(fiber.Map{"Status": "Insert", "Message": "Successfully created", "Data": res})
}

// AddMember godoc
// @Tags Crud Transaksi
// @Accept json
// @Produce json
// @Param request body request.ValidateMemberRequest true "Request"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/transaksi/addmember [post]
func AddMember(c *fiber.Ctx) error {
	member := new(models.Member)

	req := new(request.ValidateMemberRequest)

	err := c.BodyParser(req)

	if err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "error", "Message": err.Error()})
	}

	if err := config.DB.First(member, "code_member = ?", req.CodeMember).Error; err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "error", "Message": "Code Member tidak di temukan"})
	}

	if err := helper.VerivHash(member.Password, req.Password); err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "error", "Message": "Password Member Salah"})
	}

	return c.Status(200).JSON(fiber.Map{"Status": "success", "Message": "Member Valid", "Data": member})
}

func ValidateMember(idmemeber int) (data *models.Member, err error) {
	member := new(models.Member)

	if err := config.DB.First(&member, idmemeber).Error; err != nil {
		return nil, err
	}

	return member, nil
}

func ValidateVoucer(CodeVoucer string) (data *models.Voucer, err error) {
	voucher := new(models.Voucer)

	if err := config.DB.First(voucher, "code = ?", CodeVoucer).Error; err != nil {
		return nil, err
	}

	if voucher.IsActive == "false" {
		return nil, errors.New("voucher is not active")
	}

	return voucher, nil
}

// ValidasiVoucer godoc
// @Tags Crud Transaksi
// @Accept json
// @Produce json
// @Param code path string true "Voucer Code"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/transaksi/voucher/{code} [get]
func ValidasiVoucer(c *fiber.Ctx) error {
	code := c.Params("code")

	var res []response.VoucerResponse

	var voucer []models.Voucer
	if err := config.DB.First(&voucer, "code= ?", code).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(404).JSON(fiber.Map{"Status": "error", "Message": "Voucher Tidak di Temukan"})
		}
		return c.Status(500).JSON(fiber.Map{"Status": "error", "Message": err.Error()})
	}

	for _, v := range voucer {
		if v.IsActive == "false" {
			return c.Status(404).JSON(fiber.Map{"Status": "error", "Message": "Voucher Tidak Active"})
		}
		voucerResponse := response.VoucerResponse{
			ID:        v.ID,
			Code:      v.Code,
			Discount:  strconv.FormatFloat(v.Discount, 'f', 0, 64),
			StartDate: v.StartDate.Format("02/01/2006"),
			EndDate:   v.EndDate.Format("02/01/2006"),
			IsActive:  v.IsActive,
		}
		res = append(res, voucerResponse)
	}

	return c.Status(200).JSON(fiber.Map{"Status": "Validasi", "Message": "Voucher Exist", "Data": res})
}

// CalculatePoint godoc
// @Tags Crud Transaksi
// @Accept json
// @Produce json
// @Param id path string true "Member Id"
// @Param request body request.RequestBiayaTransaksi true "Request"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/transaksi/calculatePoint/{id} [post]
func CalculatePoint(c *fiber.Ctx) error {
	req := new(request.RequestBiayaTransaksi)
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	idParam := c.Params("id")

	id, err := strconv.Atoi(idParam)
	if err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	var existingMember models.Member
	if err := config.DB.First(&existingMember, id).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(fiber.StatusNotFound).JSON(fiber.Map{"Status": "Error", "Message": "ID Member tidak di temukan"})
		}
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	jumlahPPN := req.TotalPrice * 10

	total := req.TotalPrice + jumlahPPN

	rasioPoin := 1000
	poinBaru := uint(total) / uint(rasioPoin)

	existingMember.Point = poinBaru + existingMember.Point

	if err := config.DB.Save(&existingMember).Error; err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}
	return c.Status(200).JSON(fiber.Map{"Status": "Success", "Point": poinBaru})
}

// Create godoc
// @Tags Crud Transaksi
// @Accept json
// @Produce json
// @Param request body request.RequestTransaksi true "Request"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/transaksi [post]
func Create(c *fiber.Ctx) error {
	req := new(request.RequestTransaksi)
	var existingMember models.Member
	err := c.BodyParser(req)
	if err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "error", "Message": err.Error()})
	}

	var existingKaryawan models.Karyawan
	if err := config.DB.First(&existingKaryawan, req.KaryawanID).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(fiber.StatusNotFound).JSON(fiber.Map{"Status": "Error", "Message": "Id Karyawan tidak di temukan"})
		}
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	var dataVoucher *models.Voucer
	if req.CodeVoucer != "" {
		dataVoucher, _ = ValidateVoucer(req.CodeVoucer)
	}

	var dataMember *models.Member
	if req.MemberID != 0 {
		dataMember, _ = ValidateMember(req.MemberID)
		if err := config.DB.First(&existingMember, dataMember.ID).Error; err != nil {
			if errors.Is(err, gorm.ErrRecordNotFound) {
				return c.Status(fiber.StatusNotFound).JSON(fiber.Map{"Status": "Error", "Message": "ID Member tidak di temukan"})
			}
			return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
		}
	}

	transaksi := models.Transaksi{
		TotalPrice:   req.TotalPrice,
		NominalTunai: req.NominalPembayaran,
		PPN:          req.Ppn,
		Kembalian:    req.Kembalian,
	}

	if dataVoucher != nil {
		transaksi.VoucerID = dataVoucher.ID
	}

	transaksi.KaryawanID = existingKaryawan.ID

	if dataMember != nil {
		transaksi.MemberID = dataMember.ID
		/*	existingMember.Point = dataMember.Point - req.Point
			if err := config.DB.Save(&existingMember).Error; err != nil {
				return c.Status(400).JSON(fiber.Map{"Status": "error", "Message": err.Error()})
			}*/
	}

	if err := config.DB.Create(&transaksi).Error; err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "error", "Message": err.Error()})
	}
	return c.Status(200).JSON(fiber.Map{"Status": "Insert", "Message": "Successfully created", "Data": transaksi})
}

// AddBarangTransaksi godoc
// @Tags Crud Transaksi
// @Accept json
// @Produce json
// @Param request body request.RequesPembelian true "Request"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/transaksi/addBarangTransaksi/ [post]
func AddBarangTransaksi(c *fiber.Ctx) error {
	req := new(request.RequesPembelian)

	err := c.BodyParser(req)

	if err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	var existingBarang models.Barang
	if err := config.DB.Find(&existingBarang, "code_barang= ?", req.BarangID).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(fiber.StatusNotFound).JSON(fiber.Map{"Status": "Error", "Message": "ID Barang tidak di temukan"})
		}
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	var existingTransaksi models.Transaksi
	if err := config.DB.First(&existingTransaksi, req.TransaksiID).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(fiber.StatusNotFound).JSON(fiber.Map{"Status": "Error", "Message": "ID Data Transaksi tidak di temukan"})
		}
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	if existingBarang.Stock == 0 {
		return c.Status(fiber.StatusNotFound).JSON(fiber.Map{"Status": "Error", "Message": "ID Data Transaksi tidak di temukan"})
	}

	pembelianBarang := models.PembelianBarang{
		TransaksiID:   existingTransaksi.ID,
		BarangID:      existingBarang.ID,
		Quantity:      req.Quantity,
		SubTotalHarga: req.SubTotalHarga,
	}

	existingBarang.Stock = existingBarang.Stock - req.Quantity

	if err := config.DB.Save(&existingBarang).Error; err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "error", "Message": err.Error()})
	}

	if err := config.DB.Create(&pembelianBarang).Error; err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}
	return c.Status(200).JSON(fiber.Map{"Status": "Insert", "Message": "Successfully created", "Data": pembelianBarang})
}

// GetBarangTransaksi godoc
// @Tags Crud Transaksi
// @Accept json
// @Produce json
// @Param id path string true "Transaksi Id"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/transaksi/barangTransaksi/{id} [get]
func GetBarangTransaksi(c *fiber.Ctx) error {
	var pembelianBarang []models.PembelianBarang
	var barang models.Barang

	idParam := c.Params("id")

	id, err := strconv.Atoi(idParam)
	if err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	if err := config.DB.Find(&pembelianBarang, "transaksi_id = ?", id).Error; err != nil {
		return c.Status(500).JSON(fiber.Map{"Message": err.Error(), "Status": "Internal Server Error"})
	}

	if len(pembelianBarang) == 0 {
		return c.Status(404).JSON(fiber.Map{"Status": "Error", "Message": "Record not found"})
	}

	var res []response.BarangPembelianResponse

	for _, v := range pembelianBarang {
		if err := config.DB.First(&barang, v.BarangID).Error; err != nil {
			if errors.Is(err, gorm.ErrRecordNotFound) {
				return c.Status(404).JSON(fiber.Map{"Status": "Error", "Message": "Record not found"})
			}
			return c.Status(500).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
		}
		pembelianBarangResponse := response.BarangPembelianResponse{
			ID:         v.ID,
			NamaBarang: barang.Name,
			CodeBarang: barang.CodeBarang,
			Price:      barang.Price,
			Quantity:   strconv.Itoa(int(v.Quantity)),
		}
		res = append(res, pembelianBarangResponse)
	}

	return c.Status(200).JSON(fiber.Map{"Message": "Success", "Data": res})
}
