package Transaksi

import (
	"errors"
	"github.com/ProjectLSP/config"
	"github.com/ProjectLSP/internal/helper"
	"github.com/ProjectLSP/internal/models"
	"github.com/ProjectLSP/internal/request"
	"github.com/gofiber/fiber/v2"
	"gorm.io/gorm"
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

	return c.Status(200).JSON(fiber.Map{"Status": "error", "Message": "Member Valid", "Data": member})
}

func ValidateMember(idmemeber string) (data *models.Member, err error) {
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

// Calculate godoc
// @Tags Crud Transaksi
// @Accept json
// @Produce json
// @Param request body request.RequestBiayaTransaksi true "Request"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/transaksi/calculate [post]
func Calculate(c *fiber.Ctx) error {
	req := new(request.RequestBiayaTransaksi)

	_, errMember := ValidateMember(req.IdMember)
	dataVoucher, errVoucher := ValidateVoucer(req.CodeVoucer)

	if errMember != nil && len(req.IdMember) != 0 {
		return c.Status(400).JSON(fiber.Map{"Status": "Error", "Message": "Member Is Not Found"})
	}

	if errVoucher != nil && len(req.CodeVoucer) != 0 {
		return c.Status(400).JSON(fiber.Map{"Status": "Error", "Message": "Voucher Is Not Found"})
	}

	jumlahPPN := req.TotalPrice * 0.10

	total := req.TotalPrice + jumlahPPN - dataVoucher.Discount
	SubTotal := total - req.NominalPembayaran
	var poinBaru *int

	if len(req.CodeVoucer) != 0 && len(req.IdMember) != 0 {
		rasioPoin := 1000
		poinBaruInt := int(total) / rasioPoin
		poinBaru = &poinBaruInt
	}
	return c.Status(200).JSON(fiber.Map{"Status": "Success", "Total": SubTotal, "Point": poinBaru})
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

	dataVoucher, _ := ValidateVoucer(req.CodeVoucer)

	transaksi := models.Transaksi{
		TotalPrice:    req.TotalPrice,
		NominalTunai:  req.NominalPembayaran,
		PPN:           req.Ppn,
		Kembalian:     req.Kembalian,
		Point:         req.Point,
		VoucerID:      dataVoucher.ID,
		KaryawanID:    existingKaryawan.ID,
		TokoProfileID: existingKaryawan.TokoProfileID,
	}

	if err := config.DB.Create(&transaksi).Error; err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "error", "Message": err.Error()})
	}
	return c.Status(200).JSON(fiber.Map{"Status": "Insert", "Message": "Successfully created", "Data": transaksi})
}
