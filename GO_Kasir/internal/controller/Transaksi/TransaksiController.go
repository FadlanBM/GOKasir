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
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/transaksi [get]
func Index(c *fiber.Ctx) error {
	var transaksi []models.Transaksi

	if err := config.DB.Find(&transaksi).Error; err != nil {
		return c.Status(500).JSON(fiber.Map{"Message": err.Error(), "Status": "Internal Server Error"})
	}

	return c.Status(200).JSON(fiber.Map{"Message": "Success", "Data": transaksi})
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
		return c.Status(400).JSON(fiber.Map{"Status": "error", "Message": err.Error()})
	}

	if err := helper.VerivHash(member.Password, req.Password); err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "error", "Message": err.Error()})
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
			return c.Status(404).JSON(fiber.Map{"Status": "error", "Message": "Discount Tidak di Temukan"})
		}
		return c.Status(500).JSON(fiber.Map{"Status": "error", "Message": err.Error()})
	}

	for _, v := range voucer {
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

	existingMember.Point = poinBaru

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

	// Validasi Voucher
	var dataVoucher *models.Voucer
	if req.CodeVoucer != "" {
		dataVoucher, _ = ValidateVoucer(req.CodeVoucer)
	}

	// Validasi Member
	var dataMember *models.Member
	if req.MemberID != 0 {
		dataMember, _ = ValidateMember(req.MemberID)
	}

	transaksi := models.Transaksi{
		TotalPrice:   req.TotalPrice,
		NominalTunai: req.NominalPembayaran,
		PPN:          req.Ppn,
		Kembalian:    req.Kembalian,
		Point:        req.Point,
	}

	if dataVoucher != nil {
		transaksi.VoucerID = dataVoucher.ID
	}

	transaksi.KaryawanID = existingKaryawan.ID

	if dataMember != nil {
		transaksi.MemberID = dataMember.ID
	}

	if err := config.DB.Create(&transaksi).Error; err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "error", "Message": err.Error()})
	}
	return c.Status(200).JSON(fiber.Map{"Status": "Insert", "Message": "Successfully created", "Data": transaksi})
}
